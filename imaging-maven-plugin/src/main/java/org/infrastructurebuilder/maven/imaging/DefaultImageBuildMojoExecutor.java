/*
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infrastructurebuilder.maven.imaging;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.isDirectory;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.configuration.management.IBArchive.IBR;
import static org.infrastructurebuilder.ibr.IBRConstants.IBR_METADATA_FILENAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER;
import static org.infrastructurebuilder.util.core.IBUtils.copy;
import static org.infrastructurebuilder.util.core.IBUtils.readJsonObject;

import java.io.File;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.maven.PackerBean;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.PackerManifest;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.json.JSONObject;

@Named
public final class DefaultImageBuildMojoExecutor implements ImageBuildMojoExecutor {

  protected final static String PACKER_ARCHIVE_FINALIZER_CONFIG = "_PACKER_ARCHIVE_DESCRIPTOR";

  public static boolean matchArtifact(final IBRInternalDependency d, final Artifact a) {
    return d.matches(new DefaultGAV(a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getType()));
  }

  private Map<String, String> additionalEnvironment = new HashMap<>();

  private List<IBArchive> ibrArtifactData;

  private String                                      ibrHandler;
  private final Map<IBArchive, IBRInternalDependency> ibrMapping = new HashMap<>();

  private final ArchiverManager archiverManager;
  private final AutomationUtils automationUtils;

  private List<DefaultIBAuthentication> baseAuthentications = new ArrayList<>();

  private String classifier;

  private boolean cleanupOnError;

  private DefaultGAV coordinates;

  private boolean copyToOtherRegions;

  private final PackerBean data = new PackerBean();

  private String description;

  private String encoding;

  private String except;

  private String finalName;

  private final boolean force = false;

  private PackerImageBuilder image;

  private String name;

  private String only;

  private File outputDirectory;

  private boolean overwrite;

  private List<PackerManifest> packerArtifactData;

  private File packerExecutable;

  private boolean parallel;

  private PlexusContainer plexusContainer;

  private File projectBuildDirectory;

  private Properties properties;

  private List<IBRInternalDependency> requirements = new ArrayList<>();

  private Settings settings;

  private JSONObject settingsJSON;

  private boolean skip;

  private boolean skipActualPackerRun;

  private boolean skipIfEmpty;

  private Logger slf4jlog;

  private String timeout;

  private File tmpDir;

  private File varFile = null;

  private Map<String, String> vars = new HashMap<>();

  private File workingDir;

  protected IBAuthConfigBean authConfig = new IBAuthConfigBean();

  @Inject
  public DefaultImageBuildMojoExecutor(final AutomationUtils utils, final ArchiverManager archiverManager) {
    this.archiverManager = requireNonNull(archiverManager);
    this.automationUtils = requireNonNull(utils);
  }

  public AutomationUtils getAutomationUtils() {
    return automationUtils;
  }

  public void _validatePackerExecutable() {
    if (packerExecutable == null)
      throw new PackerException("No packer executable is available");
    if (!packerExecutable.isAbsolute())
      throw new PackerException("The path to the packer executable must be absolute");
    if (!packerExecutable.isFile())
      throw new PackerException("The packer executable must be a file");
    if (!packerExecutable.canExecute())
      throw new PackerException("The packer executable must be executable");
  }

  @Override
  public Optional<Map<String, Path>> execute(final String executionId, final Set<Artifact> artifacts) {

    if (executionId.contains(EXECUTION_ID))
      throw new IBException("Execution Id cannot contain the string '" + EXECUTION_ID + "'");
    getLog().log(Logger.Level.INFO,"ExecutionId : " + executionId);
    try {
      if (isSkip()) {
        getLog().log(Logger.Level.INFO,"Skipping plugin execution");
        return Optional.empty();
      }

      packerArtifactData = resolve(artifacts);
      getLog().log(Logger.Level.INFO,"Execution " + executionId + " has acquired " + packerArtifactData.size() + " elements");
      ibrArtifactData = extract(getRequirements(), artifacts);
      return data.setExecutionConfigFrom(this).executePacker(executionId);
    } catch (final Exception e) {
      throw new IBException("Packer processing exception", e);
    }
  }

  public List<IBArchive> extract(final List<IBRInternalDependency> requirements,
      final Set<Artifact> resolvedArtifacts) {

    if (!isDirectory(getWorkingDirectory())) {
      et.withTranslation(() -> createDirectories(getWorkingDirectory()));
    }

    final List<IBArchive> ibrArchives = new ArrayList<>();

    for (final IBRInternalDependency a : requirements) {
      getLog().log(Logger.Level.DEBUG,"Checking for " + a);
      final List<Artifact> list1 = new ArrayList<>();

      for (final Artifact a1 : resolvedArtifacts) {
        getLog().log(Logger.Level.DEBUG,"  vs. " + a1);
        if (matchArtifact(a, a1)) {
          list1.add(a1);
        }
      }
      if (list1.size() == 0)
        throw new IBException("No matching artifact for " + a);

      Path awd = getWorkingDirectory().toAbsolutePath();
      for (final Artifact art : list1) {
        final Path instanceWorkingDir = IBR.equals(art.getType()) ? awd.resolve(randomUUID().toString()) : awd;
        getLog().log(Logger.Level.INFO,"Writing " + art + " to " + instanceWorkingDir);
        if (a.isUnpack().orElse(false)) {
          final UnArchiver aa = et.withReturningTranslation(() -> getArchiverManager().getUnArchiver(art.getFile()));
          aa.setDestDirectory(instanceWorkingDir.toFile());
          aa.setOverwrite(a.isOverwrite());
          et.withTranslation(() -> createDirectories(aa.getDestDirectory().toPath()));
          aa.setSourceFile(art.getFile());
          getLog().log(Logger.Level.INFO,"Unpacking " + aa.getSourceFile() + " to " + instanceWorkingDir);
          aa.extract();
          a.setFile(instanceWorkingDir);
        } else {
          final Path targetPath = instanceWorkingDir.resolve(art.getFile().getName());
          getLog().log(Logger.Level.INFO,"Copying " + art.getFile().toPath() + " to " + targetPath);
          et.withTranslation(() -> copy(art.getFile().toPath(), targetPath));
          a.setFile(targetPath);
        }
        a.applyTargetDir(instanceWorkingDir);
        if (IBR.equals(art.getType())) {
          getLog().log(Logger.Level.INFO,"Reading Manifest from " + art.getFile().toPath());
          final IBArchive archive = et.withReturningTranslation(() -> new IBArchive(
              readJsonObject(instanceWorkingDir.resolve(IBR_METADATA_FILENAME)), instanceWorkingDir));

          ibrMapping.put(archive, a);
          ibrArchives.add(archive);
        }
      }
    }

    return ibrArchives;
  }

  @Override
  public Map<String, String> getAdditionalEnvironment() {
    return additionalEnvironment;
  }

  @Override
  public PrintStream getAdditionalPrintStream() {
    return System.out;
  }

  @Override
  public List<IBArchive> getIBRArchives() {
    return ibrArtifactData;
  }

  @Override
  public String getIBRHandler() {
    return ibrHandler;
  }

  @Override
  public Map<IBArchive, IBRInternalDependency> getIBRMapping() {
    return ibrMapping;
  }

  @Override
  public ArchiverManager getArchiverManager() {
    return archiverManager;
  }

  @Override
  public IBAuthConfigBean getAuthFileWriter() {
    return authConfig;
  }

  @Override
  public List<DefaultIBAuthentication> getBaseAuthentications() {
    return baseAuthentications;
  }

  @Override
  public String getClassifier() {
    return classifier;
  }

  @Override
  public DefaultGAV getCoordinates() {
    return coordinates;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Charset getEncoding() {
    return encoding != null ? Charset.forName(encoding) : Charset.defaultCharset();
  }

  @Override
  public String getExcept() {
    return except;
  }

  @Override
  public String getFinalName() {
    return finalName;
  }

  @Override
  public PackerImageBuilder getImage() {
    return image;
  }

  public Logger getLog() {
    return slf4jlog;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOnly() {
    return only;
  }

  @Override
  public Path getOutputDirectory() {
    return outputDirectory.toPath();
  }

  @Override
  public Path getPackerExecutable() {
    _validatePackerExecutable();
    return packerExecutable.toPath();
  }

  @Override
  public List<PackerManifest> getPackerManifests() {
    return packerArtifactData;
  }

  @Override
  public PlexusContainer getPlexusContainer() {
    return plexusContainer;
  }

  @Override
  public File getProjectBuildDirectory() {
    return projectBuildDirectory;
  }

  @Override
  public Properties getProperties() {
    return properties;
  }

  @Override
  public List<IBRInternalDependency> getRequirements() {
    for (final IBRInternalDependency i : requirements)
      if (!i.isUnpack().isPresent()) {
        i.setUnpack(isUnpackByDefault());
      }
    return requirements;
  }

  @Override
  public JSONObject getSettingsJSON() {
    return settingsJSON;
  }

  @Override
  public String getTimeout() {
    return timeout;
  }

  @Override
  public Path getTmpDir() {
    return tmpDir.toPath();
  }

  @Override
  public File getVarFile() {
    return varFile;
  }

  @Override
  public Map<String, String> getVars() {
    return vars;
  }

  @Override
  public Path getWorkingDirectory() {
    return workingDir.toPath();
  }

  @Override
  public boolean isCleanupOnError() {
    return cleanupOnError;
  }

  @Override
  public boolean isCopyToOtherRegions() {
    return copyToOtherRegions;
  }

  @Override
  public boolean isForce() {
    return force;
  }

  @Override
  public boolean isOverwrite() {
    return overwrite;
  }

  @Override
  public boolean isParallel() {
    return parallel;
  }

  @Override
  public boolean isSkip() {
    return skip;
  }

  @Override
  public boolean isSkipActualPackerRun() {
    return skipActualPackerRun;
  }

  @Override
  public boolean isSkipIfEmpty() {
    return skipIfEmpty;
  }

  @Override
  public boolean isUnpackByDefault() {
    return true;
  }

  public List<PackerManifest> resolve(final Set<Artifact> resolvedArtifacts) {

    final List<PackerManifest> psm = new ArrayList<>();
    for (final org.apache.maven.artifact.Artifact d : resolvedArtifacts)
      if (PACKER.equals(d.getType())) {
        getLog().log(Logger.Level.INFO,"Reading manifest from " + d.getFile().toPath());
        et.withTranslation(
            () -> psm.add(new PackerManifest(readJsonObject(d.getFile().toPath().toAbsolutePath()), plexusContainer)));
      }

    getLog().log(Logger.Level.INFO,"Got \n" + psm.toString());

    return psm;
  }

  @Override
  public ImageBuildMojoExecutor setAdditionalEnvironment(final Map<String, String> additionalEnvironment) {
    this.additionalEnvironment = additionalEnvironment;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setIBRArtifactData(final List<IBArchive> ibrArtifactData) {
    this.ibrArtifactData = ibrArtifactData;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setIBRHandler(final String ibrHandler) {
    this.ibrHandler = ibrHandler;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setAuthConfig(final IBAuthConfigBean authConfig) {
    this.authConfig = authConfig;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setBaseAuthentications(final List<DefaultIBAuthentication> baseAuthentications) {
    this.baseAuthentications = baseAuthentications;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setClassifier(final String classifier) {
    this.classifier = classifier;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setCleanupOnError(final boolean cleanupOnError) {
    this.cleanupOnError = cleanupOnError;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setCoordinates(final DefaultGAV coords) {
    coordinates = coords;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setCopyToOtherRegions(final boolean copyToOtherRegions) {
    this.copyToOtherRegions = copyToOtherRegions;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setDescription(final String description) {
    this.description = description;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setEncoding(final String encoding) {
    this.encoding = requireNonNull(encoding);
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setExcept(final String except) {
    this.except = except;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setFinalName(final String finalName) {
    this.finalName = finalName;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setImage(final PackerImageBuilder image) {
    this.image = requireNonNull(image);
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setLog(final Logger slf4jlog) {
    this.slf4jlog = slf4jlog;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setOnly(final String only) {
    this.only = only;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setOutputDirectory(final File outputDirectory) {
    this.outputDirectory = outputDirectory;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setOverwrite(final boolean overwrite) {
    this.overwrite = overwrite;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setPackerArtifactData(final List<PackerManifest> packerArtifactData) {
    this.packerArtifactData = packerArtifactData;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setPackerExecutable(final File packerExecutable) {
    this.packerExecutable = requireNonNull(packerExecutable);
    _validatePackerExecutable();
    return this;

  }

  @Override
  public ImageBuildMojoExecutor setParallel(final boolean parallel) {
    this.parallel = parallel;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setPlexusContainer(final PlexusContainer plexusContainer) {
    this.plexusContainer = plexusContainer;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setProjectBuildDirectory(final File projectBuildDirectory) {
    this.projectBuildDirectory = projectBuildDirectory;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setProperties(final Properties properties) {
    this.properties = properties;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setRequirements(final List<IBRInternalDependency> requirements) {
    this.requirements = requireNonNull(requirements);
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSettings(final Settings settings) {
    this.settings = requireNonNull(settings);
    final JSONObject retval = new JSONObject();
    for (final Server s : this.settings.getServers()) {
      retval.put(s.getId(), new JSONObject(s));
    }
    settingsJSON = retval;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSettingsJSON(final JSONObject settingsJSON) {
    this.settingsJSON = settingsJSON;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSkip(final boolean skip) {
    this.skip = skip;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSkipActualPackerRun(final boolean skipActualPackerRun) {
    this.skipActualPackerRun = skipActualPackerRun;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSkipIfEmpty(final boolean skipIfEmpty) {
    this.skipIfEmpty = skipIfEmpty;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setTimeout(final String timeout) {
    this.timeout = timeout;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setTmpDir(final File tmpDir) {
    this.tmpDir = tmpDir;
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setVarFile(final File varFile) {
    this.varFile = requireNonNull(varFile);
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setVars(final Map<String, String> vars) {
    this.vars = requireNonNull(vars);
    return this;
  }

  @Override
  public ImageBuildMojoExecutor setWorkingDir(final File workingDir) {
    this.workingDir = workingDir;
    return this;
  }
}