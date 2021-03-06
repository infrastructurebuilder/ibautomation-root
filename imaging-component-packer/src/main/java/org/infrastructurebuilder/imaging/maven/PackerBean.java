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
package org.infrastructurebuilder.imaging.maven;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.isWritable;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.MR_PARAMS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NO_TIMEOUT_SPECIFIED;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER;
import static org.infrastructurebuilder.imaging.maven.PackerManifest.runAndGenerateManifest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.ImageBuilder;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.infrastructurebuilder.imaging.ibr.PackerGenericIBRArchiveProvisioner;
import org.infrastructurebuilder.imaging.ibr.PackerIBRProvisioner;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducerFactory;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class PackerBean {
  public final static Function<Path, Path> forceCreateDir = i -> {
    final Optional<Path> p = ofNullable(i).map(Path::toAbsolutePath);
    p.ifPresent(pp -> {
      if (!exists(pp, NOFOLLOW_LINKS)) {
        try {
          Files.createDirectories(pp);
        } catch (final IOException e) {
          throw new PackerException("Cannot create " + pp.toAbsolutePath(), e);
        }
      }
    });
    p.ifPresent(pp -> {
      if (!(exists(pp, NOFOLLOW_LINKS) && Files.isDirectory(pp, NOFOLLOW_LINKS) && isWritable(pp)))
        throw new PackerException(pp.toString() + " is not a writable, non-linked, directory");
    });
    return p.orElseThrow(() -> new PackerException("Cannot create or locate " + i));

  };

  public static void failNull(final Object o, final String string) {
    if (o == null)
      throw new PackerException("No " + string);
  }

  private Map<String, String>                   additionalEnvironment = new HashMap<>();
  private List<IBArchive>                       ibrArchives;
  private String                                ibrHandler;
  private Map<IBArchive, IBRInternalDependency> internalDependencies;
  private ArchiverManager                       archiverManager;
  private IBAuthenticationProducerFactory       authFactory;
  private Optional<String>                      classifier            = Optional.empty();
  private boolean                               cleanupOnError        = false;
  private Optional<GAV>                         coords                = Optional.empty();
  private boolean                               copyToOtherRegions;
  private String                                description;
  private Charset                               encoding              = StandardCharsets.UTF_8;
  private Optional<String>                      except                = Optional.empty();
  private String                                finalName;
  private boolean                               force                 = false;
  private Supplier<PackerManifest>              generator;
  private ImageBuilder                          image;
  private Logger                                log                   = System.getLogger(PackerBean.class.getName());
  private String                                name;
  private Optional<String>                      only                  = Optional.empty();
  private Path                                  outputDirectory;
  private List<PackerManifest>                  packerArtifactData;
  private Path                                  packerExecutable;

  private PackerFactory                         packerFactory;

  private boolean                               parallel              = true;
  private PlexusContainer                       plexusContainer;

  private Optional<Path>                        projectBuildDirectory = Optional.empty();

  private Properties                            properties;

  private List<IBRInternalDependency>           requirements;

  private Map<String, String>                   runtimeEnvironment;

  private JSONObject                            settingsJSON;

  private boolean                               skipActualPackerRun   = false;

  private Optional<PrintStream>                 stdOut                = Optional.empty();

  private Path                                  tempDirectory;

  private Duration                              timeout;

  private Path                                  varFile;

  private Map<String, String>                   vars;
  private Path                                  workingDirectory;

  private VersionedProcessExecutionFactory      vpef;

  public PackerBean setVersionedProcessExecutionFactory(VersionedProcessExecutionFactory vpef) {
    this.vpef = vpef;
    return this;
  }

  public Map<String, String> getAdditionalEnvironment() {
    return additionalEnvironment;
  }

  public String getIBRHandler() {
    return ofNullable(ibrHandler).orElse(PackerGenericIBRArchiveProvisioner.GENERIC_IBR);
  }

  public Optional<ArchiverManager> getArchiverManager() {
    return ofNullable(archiverManager);
  }

  public IBAuthenticationProducerFactory getAuthFactory() {
    return authFactory;
  }

  public Optional<String> getClassifier() {
    return classifier;
  }

  public GAV getCoords() {
    return coords.orElseThrow(() -> new PackerException("no coords"));
  }

  public Charset getEncoding() {
    return encoding;
  }

  public Optional<String> getExcept() {
    return except;
  }

//  public Optional<Supplier<PackerManifest>> getGenerator() {
//    return ofNullable(generator);
//  }
//
  public Optional<ImageBuilder> getImage() {
    return ofNullable(image);
  }

  public Logger getLog() {
    return log;
  }

  public Optional<String> getOnly() {
    return only;
  }

  public Path getOutputDirectory() {
    return outputDirectory;
  }

  public List<String> getParams() {
    final List<String> packerParams = new ArrayList<>();
    packerParams.addAll(MR_PARAMS);
    getOnly().ifPresent(o -> packerParams.add("-only=" + o));
    if (isForce()) {
      packerParams.add("-force");
    }
    getExcept().ifPresent(e -> packerParams.add("-except=" + e));
    packerParams.add("-on-error=" + (isCleanupOnError() ? "cleanup" : "abort"));
    if (getLog().isLoggable(Logger.Level.DEBUG)) {
      packerParams.add("-debug");
    }
    packerParams.add("-parallel=" + isParallel());

    getVarFile().ifPresent(v -> {
      if (exists(v) && isRegularFile(v) && isReadable(v)) {
        packerParams.add("-var-file=" + v);
      }
    });

    return packerParams;
  }

  public PlexusContainer getPlexusContainer() {
    return ofNullable(plexusContainer).orElseThrow(() -> new PackerException("No plexus container available"));
  }

  public Optional<Path> getProjectBuildDirectory() {
    return projectBuildDirectory;
  }

  public Optional<List<IBRInternalDependency>> getRequirements() {
    return ofNullable(requirements);
  }

  public Map<String, String> getRuntimeEnvironment() {
    if (runtimeEnvironment == null) {

      runtimeEnvironment = new HashMap<>(getAdditionalEnvironment());
      final Map<String, String> m = new HashMap<>();
      m.putAll(getAuthFactory().getEnvironmentForAllTypes());
      m.putAll(System.getenv());
      runtimeEnvironment.putAll(m);

      getLog();
    }
    return runtimeEnvironment;
  }

  public Optional<Duration> getTimeout() {
    Optional<Duration> t = ofNullable(timeout);
    if (t.isPresent() && (t.get().isZero() || t.get().isNegative())) {
      t = Optional.empty();
    }
    if (!t.isPresent()) {
      getLog().log(Logger.Level.WARNING, NO_TIMEOUT_SPECIFIED);
    }
    timeout = t.orElse(null);
    return ofNullable(timeout);
  }

  public Optional<Path> getVarFile() {
    return ofNullable(varFile);
  }

  public Optional<Map<String, String>> getVars() {
    return ofNullable(vars);
  }

  public Path getWorkingDirectory() {
    return workingDirectory;
  }

  public boolean isCleanupOnError() {
    return cleanupOnError;
  }

  public boolean isForce() {
    return force;
  }

  public boolean isParallel() {
    return parallel;
  }

  public boolean isSkipActualPackerRun() {
    return skipActualPackerRun;
  }

  public void setAdditionalEnvironment(final Map<String, String> additionalEnvironment) {
    this.additionalEnvironment = requireNonNull(additionalEnvironment);
  }

  public void setIBRArchives(final List<IBArchive> ibrARchives) {
    ibrArchives = requireNonNull(ibrARchives);
  }

  public void setClassifier(final String classifier) {
    this.classifier = ofNullable(classifier).map(s -> s.trim()).filter(s -> s.length() > 0);
  }

  public void setCleanupOnError(final boolean cleanupOnError) {
    this.cleanupOnError = cleanupOnError;
  }

  public void setCoords(final GAV defGAV) {
    coords = ofNullable(defGAV);
  }

  public void setEncoding(final Charset encoding) {
    this.encoding = requireNonNull(encoding);
  }

  public void setExcept(final String except) {
    this.except = ofNullable(except);
  }

  @SuppressWarnings("deprecation")
  public final PackerBean setExecutionConfigFrom(final ImageBuildExecutionConfig c) throws ComponentLookupException {

    archiverManager = requireNonNull(c.getArchiverManager());
    properties = requireNonNull(c.getProperties());
    image = requireNonNull(c.getImage());
    setRequirements(c.getRequirements());
    setPlexusContainer(requireNonNull(c.getPlexusContainer()));

    setSettingsJSON(requireNonNull(c.getSettingsJSON()));
    tempDirectory = forceCreateDir.apply(requireNonNull(c.getTmpDir()));
    final IBAuthConfigBean iBAuthConfigBean = requireNonNull(c.getAuthFileWriter());
    authFactory = setupAuthFactory(iBAuthConfigBean,
        iBAuthConfigBean.mergedUpdatedAuthList(settingsJSON, requireNonNull(c.getBaseAuthentications())));

    internalDependencies = requireNonNull(c.getIBRMapping());
    setPackerManifests(c.getPackerManifests());
    setIBRHandler(c.getIBRHandler());
    setIBRArchives(c.getIBRArchives());
    setCoords(c.getCoordinates());
    setEncoding(c.getEncoding());
    setClassifier(c.getClassifier());
    setTimeout(c.getTimeout());
    setProjectBuildDirectory(c.getProjectBuildDirectory());
    setWorkingDirectory(c.getWorkingDirectory());
    setPackerExecutable(c.getPackerExecutable());
    setOutputDirectory(c.getOutputDirectory());
    setFinalName(c.getFinalName());
    setAdditionalEnvironment(c.getAdditionalEnvironment());
    setSkipActualPackerRun(c.isSkipActualPackerRun());
    setOnly(c.getOnly());
    setForce(c.isForce());
    setExcept(c.getExcept());
    setCleanupOnError(c.isCleanupOnError());
    setParallel(c.isParallel());
    setVarFile(c.getVarFile());
    setVars(c.getVars());
    stdOut = ofNullable(c.getAdditionalPrintStream());
    name = requireNonNull(c.getName());
    description = requireNonNull(c.getDescription());
    copyToOtherRegions = c.isCopyToOtherRegions();

    packerFactory = getPackerFactory();
    return this;
  }

  IBAuthenticationProducerFactory setupAuthFactory(IBAuthConfigBean iBAuthConfigBean, List<IBAuthentication> list)
      throws ComponentLookupException {
    IBAuthenticationProducerFactory af = getPlexusContainer().lookup(IBAuthenticationProducerFactory.class, "default");
    af.setAuthentications(list);
    af.setTemp(iBAuthConfigBean.getTempDirectory());
    return af;
  }

  public void setFinalName(final String finalName) {
    this.finalName = requireNonNull(finalName);
  }

  public void setForce(final boolean force) {
    this.force = force;
  }

  public void setLog(final Logger slf4jFromMavenLogger) {
    log = requireNonNull(slf4jFromMavenLogger);
  }

  public void setOnly(final String only) {
    this.only = ofNullable(only);

  }

  public void setOutputDirectory(final Path o) {
    outputDirectory = forceCreateDir.apply(requireNonNull(o));
  }

  public void setPackerExecutable(final Path packerExecutable) {
    this.packerExecutable = et.withReturningTranslation(() -> {
      return requireNonNull(packerExecutable).toRealPath().toAbsolutePath();
    });
  }

  public void setPackerManifests(final List<PackerManifest> packerManifests) {
    packerArtifactData = requireNonNull(packerManifests);
  }

  public void setParallel(final boolean parallel) {
    this.parallel = parallel;
  }

  public void setPlexusContainer(final PlexusContainer plexusContainer) {
    this.plexusContainer = requireNonNull(plexusContainer);
  }

  public void setProjectBuildDirectory(final File pbd) {
    projectBuildDirectory = ofNullable(pbd).map(File::toPath).map(Path::toAbsolutePath);
  }

  public void setRequirements(final List<IBRInternalDependency> requirements) {
    this.requirements = requireNonNull(requirements);
  }

  public void setSettingsJSON(final JSONObject settingsJSON) {
    this.settingsJSON = requireNonNull(settingsJSON);
  }

  public void setSkipActualPackerRun(final boolean skipActualPackerRun) {
    this.skipActualPackerRun = skipActualPackerRun;
  }

  public void setTimeout(final String optional) {
    timeout = ofNullable(optional).map(Duration::parse).orElse(null);
  }

  public void setVarFile(final File varFile) {
    this.varFile = ofNullable(varFile).map(File::toPath).map(Path::toAbsolutePath).orElse(null);
  }

  public void setVars(final Map<String, String> vars) {
    this.vars = requireNonNull(vars);
  }

  public void setWorkingDirectory(final Path workingDir) {
    workingDirectory = forceCreateDir.apply(requireNonNull(workingDir));
  }

  public Path writeJSON(final JSONObject o) throws JSONException, IOException {

    final Path p = getOutputDirectory().resolve(UUID.randomUUID().toString() + ".json");
    Files.write(p, Arrays.asList(o.toString(2).split(System.lineSeparator())), getEncoding(),
        StandardOpenOption.CREATE);
    return p;

  }

  private void setIBRHandler(final String ibrHandler) {
    this.ibrHandler = ibrHandler;
  }

  public Optional<Map<String, Path>> executePacker(final String executionId) throws Exception {
    validate(executionId);
    Map<String, Path> finalMap = null;
    if (!Files.isDirectory(getWorkingDirectory()))
      throw new PackerException("No working dir " + getWorkingDirectory());
    final Path pFile = packerFactory.get();
    if (isSkipActualPackerRun()) {
      getLog().log(Logger.Level.WARNING, "Skipping packer run.  Target packer file -> " + pFile.toAbsolutePath());
    } else {
      finalMap = new HashMap<>();
      final PackerManifest packerManifest = runAndGenerateManifest(packerFactory, tempDirectory, name, description,
          getCoords(), getTimeout(), stdOut, getParams(), getRuntimeEnvironment());

      finalMap.put(PACKER, writeJSON(packerManifest.asJSON()));
      finalMap.putAll(packerFactory.collectAllForcedOutput());
    }
    authFactory.deleteAuthFiles();
    return ofNullable(finalMap);
  }

  PackerFactory getPackerFactory() throws PackerException, ComponentLookupException {
    if (packerFactory == null) {

      packerFactory = new DefaultPackerFactory(

          getVersionedProcessExecutionFactory(),

          getPlexusContainer(),

          getLog(),

          getProjectBuildDirectory().get(),

          getWorkingDirectory(),

          requireNonNull(packerArtifactData, "packer manifest data for getPackerFactory"),

          getImage().orElseThrow(() -> new PackerException("No Image to build")),

          authFactory,

          packerExecutable,

          properties,

          getCoords(), requirements, copyToOtherRegions);
      if (ibrArchives.size() > 0) {
        for (final IBArchive ibr : ibrArchives) {
          final IBRInternalDependency d = internalDependencies.get(ibr);
          PackerIBRProvisioner        p;
          try {
            p = ((PackerIBRProvisioner) getPlexusContainer().lookup(PackerProvisioner.class, getIBRHandler()));
            p.setLog(getLog());
          } catch (final ComponentLookupException e) {
            throw new PackerException("Cannot location IBR Handler " + getIBRHandler());
          }
          p.setWorkingRootDirectory(d.getTargetDir().get());
          for (final PackerProvisioner pp : p.applyArchive(ibr)) {
            packerFactory.addProvisioner(pp);
          }
        }
      }
    }
    return packerFactory;
  }

  private VersionedProcessExecutionFactory getVersionedProcessExecutionFactory() {
    return this.vpef;
  }

  void validate(final String executionId) {
    if (requireNonNull(executionId).contains("executionId"))
      throw new PackerException("Execution ids cannot contain the string 'executionId'");
    failNull(finalName, "finalName");
    failNull(log, "log");
    failNull(outputDirectory, "outputDirectory");
    failNull(packerExecutable, "packerExecutable");
    failNull(settingsJSON, "settingsJSON");
    failNull(tempDirectory, "tempDirectory");
    failNull(workingDirectory, "workingDirectory");
  }
}
