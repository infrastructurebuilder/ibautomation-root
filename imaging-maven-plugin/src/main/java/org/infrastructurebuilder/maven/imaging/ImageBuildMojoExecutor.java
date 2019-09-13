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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.maven.ImageBuildExecutionConfig;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.PackerManifest;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.json.JSONObject;
import org.slf4j.Logger;

public interface ImageBuildMojoExecutor extends ImageBuildExecutionConfig {

  Optional<Map<String, Path>> execute(String executionId, Set<Artifact> artifacts);

  ImageBuildMojoExecutor setAdditionalEnvironment(Map<String, String> additionalEnvironment);

  ImageBuildMojoExecutor setIBRArtifactData(List<IBArchive> ibrArtifactData);

  ImageBuildMojoExecutor setIBRHandler(String ibrHandler);

  ImageBuildMojoExecutor setAuthConfig(IBAuthConfigBean authConfig);

  ImageBuildMojoExecutor setBaseAuthentications(List<DefaultIBAuthentication> baseAuthentications);

  ImageBuildMojoExecutor setClassifier(String classifier);

  ImageBuildMojoExecutor setCleanupOnError(boolean cleanupOnError);

  ImageBuildMojoExecutor setCoordinates(DefaultGAV coords);

  ImageBuildMojoExecutor setCopyToOtherRegions(boolean copyToOtherRegions);

  ImageBuildMojoExecutor setDescription(String description);

  ImageBuildMojoExecutor setEncoding(String encoding);

  ImageBuildMojoExecutor setExcept(String except);

  ImageBuildMojoExecutor setFinalName(String finalName);

  ImageBuildMojoExecutor setImage(PackerImageBuilder image);

  ImageBuildMojoExecutor setLog(Logger slf4jlog);

  ImageBuildMojoExecutor setName(String name);

  ImageBuildMojoExecutor setOnly(String only);

  ImageBuildMojoExecutor setOutputDirectory(File outputDirectory);

  ImageBuildMojoExecutor setOverwrite(boolean overwrite);

  ImageBuildMojoExecutor setPackerArtifactData(List<PackerManifest> packerArtifactData);

  ImageBuildMojoExecutor setPackerExecutable(File packerExecutable);

  ImageBuildMojoExecutor setParallel(boolean parallel);

  ImageBuildMojoExecutor setPlexusContainer(PlexusContainer plexusContainer);

  ImageBuildMojoExecutor setProjectBuildDirectory(File projectBuildDirectory);

  ImageBuildMojoExecutor setProperties(Properties properties);

  ImageBuildMojoExecutor setRequirements(List<IBRInternalDependency> requirements);

  ImageBuildMojoExecutor setSettings(Settings settings);

  ImageBuildMojoExecutor setSettingsJSON(JSONObject settingsJSON);

  ImageBuildMojoExecutor setSkip(boolean skip);

  ImageBuildMojoExecutor setSkipActualPackerRun(boolean skipActualPackerRun);

  ImageBuildMojoExecutor setSkipIfEmpty(boolean skipIfEmpty);

  ImageBuildMojoExecutor setTimeout(String timeout);

  ImageBuildMojoExecutor setTmpDir(File tmpDir);

  ImageBuildMojoExecutor setVarFile(File varFile);

  ImageBuildMojoExecutor setVars(Map<String, String> vars);

  ImageBuildMojoExecutor setWorkingDir(File workingDir);

}