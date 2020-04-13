/**
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

import static java.util.Optional.ofNullable;
import static org.apache.maven.plugins.annotations.LifecyclePhase.COMPILE;
import static org.apache.maven.plugins.annotations.ResolutionScope.RUNTIME;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER;
import static org.infrastructurebuilder.imaging.ibr.PackerGenericIBRArchiveProvisioner.GENERIC_IBR;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.PackerManifest;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.logging.SLF4JFromMavenLogger;

@Mojo(name = "imaging", requiresProject = true, threadSafe = false, requiresDependencyResolution = RUNTIME, defaultPhase = COMPILE)
public final class ImageBuildMojo extends AbstractMojo implements Contextualizable {
  public final static BiFunction<Artifact, String, DefaultGAV> toDefaultGAV = (art, classifier) -> {
    return new DefaultGAV(art.getGroupId(), art.getArtifactId(), classifier, art.getVersion(), PACKER);
  };

  protected final static String PACKER_ARCHIVE_FINALIZER_CONFIG = "_PACKER_ARCHIVE_DESCRIPTOR";

  private List<IBArchive> ibrArtifactData;

  private PlexusContainer plexusContainer;

  @Parameter
  Map<String, String> additionalEnvironment = new HashMap<>();

  @Parameter(defaultValue = GENERIC_IBR)
  String ibrHandler;

  final Map<IBArchive, IBRInternalDependency> ibrMapping = new HashMap<>();

  @Parameter
  IBAuthConfigBean authConfig = new IBAuthConfigBean();

  @Parameter(property = "image.baseAuthentications")
  List<DefaultIBAuthentication> baseAuthentications = new ArrayList<>();

  @Parameter(property = "image.classifier")
  String classifier;

  @Parameter(property = "image.cleanupOnError", defaultValue = "true")
  boolean cleanupOnError;

  @Parameter(property = "image.copy.regions", required = false, defaultValue = "true")
  boolean copyToOtherRegions;

  @Parameter(property = "image.description", defaultValue = "${project.description}")
  String description;

  @Parameter(property = "image.encoding", defaultValue = "${project.build.sourceEncoding}")
  String encoding;

  @Parameter(property = "image.except")
  String except;

  @Component
  ImageBuildMojoExecutor executor;

  @Parameter(property = "image.finalName", required = true, defaultValue = "${project.build.finalName}")
  String finalName;

  @Parameter(property = "image.force", required = false)
  final boolean      force = false;

  @Parameter(required = true)
  PackerImageBuilder image;

  @Component
  MavenProjectHelper mavenProjectHelper;

  @Parameter(defaultValue = "${mojoExecution}", readonly = true)
  MojoExecution mojo;

  @Parameter(property = "image.only")
  String only;

  @Parameter(property = "image.outputDirectory", defaultValue = "${project.build.directory}", required = true)
  File outputDirectory;

  @Parameter(property = "image.overwrite", defaultValue = "false")
  boolean overwrite;

  List<PackerManifest> packerArtifactData;

  @Parameter(property = "image.executable", required = true, defaultValue = "${project.build.directory}/packer")
  File packerExecutable;

  @Parameter(property = "image.parallel", defaultValue = "true", required = false)
  boolean parallel;

  @Parameter(property = "project", readonly = true, required = true)
  MavenProject project;

  @Parameter(defaultValue = "${project.build.directory}", readonly = true)
  File projectBuildDirectory;

  @Parameter
  List<IBRInternalDependency> requirements = new ArrayList<>();

  @Parameter(defaultValue = "${settings}", readonly = true, required = true)
  Settings settings;

  @Parameter(property = "image.skip", defaultValue = "false")
  boolean skip;

  @Parameter(property = "image.skipActualPackerRun", defaultValue = "false")
  boolean skipActualPackerRun;

  @Parameter(property = "image.skipIfEmpty", defaultValue = "true")
  boolean skipIfEmpty;

  @Parameter(property = "image.timeout")
  String timeout;

  @Parameter(property = "image.tmpdir", defaultValue = "${project.build.directory}/packer-tmp")
  File tmpDir;

  @Parameter(property = "image.var-file", required = false)
  File varFile = null;

  @Parameter(property = "image.vars", required = false)
  Map<String, String> vars = new HashMap<>();

  @Parameter(property = "image.working.dir", required = true, defaultValue = "${project.build.directory}/packer-work")
  File workingDir;

  public ImageBuildMojoExecutor applyParameters() {
    return executor
        // Addl env
        .setAdditionalEnvironment(additionalEnvironment)
        // Auth config
        .setAuthConfig(authConfig)
        // Base authentications
        .setBaseAuthentications(baseAuthentications)
        // Classifications
        .setClassifier(classifier)
        // Description
        .setDescription(description)
        // Encodinmg
        .setEncoding(encoding)
        // Final name
        .setFinalName(finalName)
        // Image
        .setImage(image)
        // Output dir
        .setOutputDirectory(outputDirectory)
        // Overwrite?
        .setOverwrite(overwrite)
        // PAcker executable
        .setPackerExecutable(packerExecutable)
        // REquirements
        .setRequirements(requirements)
        // The settings
        .setSettings(settings)
        // Skip packer run
        .setSkipActualPackerRun(skipActualPackerRun)
        // Timeout
        .setTimeout(timeout)
        // Temp dir
        .setTmpDir(tmpDir)
        // Var file
        .setVarFile(varFile)
        // Vars
        .setVars(vars)
        // Working dir
        .setWorkingDir(workingDir)
        // Artifact data
        .setIBRArtifactData(ibrArtifactData)
        // Handler
        .setIBRHandler(ibrHandler)
        // Cleanup on error?
        .setCleanupOnError(cleanupOnError)
        // Copy to other regions?
        .setCopyToOtherRegions(copyToOtherRegions)
        // "Except"s
        .setExcept(except)
        // Name
        .setName(project.getName())
        // Project properties
        .setProperties(project.getProperties())
        // Coords
        .setCoordinates(toDefaultGAV.apply(project.getArtifact(), classifier))
        // "only"?
        .setOnly(only)
        // Packer artifactData
        .setPackerArtifactData(packerArtifactData)
        // Parallel?
        .setParallel(parallel)
        // Container FIXME
        .setPlexusContainer(plexusContainer)
        // Project build dir
        .setProjectBuildDirectory(projectBuildDirectory)
        // Skip if empty
        .setSkipIfEmpty(skipIfEmpty)
        // Logger
        .setLog(new SLF4JFromMavenLogger(getLog()));
  }

  @Override
  public void contextualize(final Context context) throws ContextException {
    plexusContainer = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    final String executionId = mojo.getExecutionId();
    if (executionId.contains("executionId"))
      throw new MojoExecutionException("Execution Id cannot contain the string 'executionId'");
    getLog().info("ExecutionId : " + executionId);
    try {
      if (skip) {
        getLog().info("Skipping plugin execution");
        return;
      }
      applyParameters().execute(executionId, project.getArtifacts()).ifPresent(out -> {
        ofNullable(out.get(PACKER)).ifPresent(finalPath -> {
          et.withTranslation(() -> {
            if (skipIfEmpty && Files.size(finalPath) == 0) {
              getLog().info("Skipping packaging of the " + PACKER);
            } else if (classifier != null) {
              mavenProjectHelper.attachArtifact(project, PACKER, classifier, finalPath.toFile());
            } else {
              if (project.getArtifact().getFile() != null && project.getArtifact().getFile().isFile())
                throw new PackerException("You have to use a classifier "
                    + "to attach supplemental artifacts to the project instead of replacing them.");
              project.getArtifact().setFile(finalPath.toFile());
            }
          });
        });
        out.keySet().stream().filter(k -> !PACKER.equals(k)).filter(key -> Files.exists(out.get(key)))
            .forEach(key -> mavenProjectHelper.attachArtifact(project, key, null, out.get(key).toFile()));
      });
    } catch (final Exception e) {
      throw new MojoFailureException("Packer processing exception", e);
    }
  }

  public Settings getSettings() {
    return settings;
  }

}