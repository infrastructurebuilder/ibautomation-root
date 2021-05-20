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

import java.io.File;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.PackerManifest;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.json.JSONObject;

public class FakePackerBuildMojoExecutor implements ImageBuildMojoExecutor {

  @Override
  public Optional<Map<String, Path>> execute(final String executionId, final Set<Artifact> artifacts) {

    return Optional.of(new HashMap<>());
  }

  @Override
  public Map<String, String> getAdditionalEnvironment() {

    return null;
  }

  @Override
  public PrintStream getAdditionalPrintStream() {

    return null;
  }

  @Override
  public List<IBArchive> getIBRArchives() {

    return null;
  }

  @Override
  public String getIBRHandler() {

    return null;
  }

  @Override
  public Map<IBArchive, IBRInternalDependency> getIBRMapping() {

    return null;
  }

  @Override
  public ArchiverManager getArchiverManager() {

    return null;
  }

  @Override
  public IBAuthConfigBean getAuthFileWriter() {

    return null;
  }

  @Override
  public List<DefaultIBAuthentication> getBaseAuthentications() {

    return null;
  }

  @Override
  public String getClassifier() {

    return null;
  }

  @Override
  public GAV getCoordinates() {

    return null;
  }

  @Override
  public String getDescription() {

    return null;
  }

  @Override
  public Charset getEncoding() {

    return null;
  }

  @Override
  public String getExcept() {

    return null;
  }

  @Override
  public String getFinalName() {

    return null;
  }

  @Override
  public PackerImageBuilder getImage() {

    return null;
  }

  @Override
  public String getName() {

    return null;
  }

  @Override
  public String getOnly() {

    return null;
  }

  @Override
  public Path getOutputDirectory() {

    return null;
  }

  @Override
  public Path getPackerExecutable() {

    return null;
  }

  @Override
  public List<PackerManifest> getPackerManifests() {

    return null;
  }

  @Override
  public PlexusContainer getPlexusContainer() {

    return null;
  }

  @Override
  public File getProjectBuildDirectory() {

    return null;
  }

  @Override
  public Properties getProperties() {

    return null;
  }

  @Override
  public List<IBRInternalDependency> getRequirements() {

    return null;
  }

  @Override
  public JSONObject getSettingsJSON() {

    return null;
  }

  @Override
  public String getTimeout() {

    return null;
  }

  @Override
  public Path getTmpDir() {

    return null;
  }

  @Override
  public File getVarFile() {

    return null;
  }

  @Override
  public Map<String, String> getVars() {

    return null;
  }

  @Override
  public Path getWorkingDirectory() {

    return null;
  }

  @Override
  public boolean isCleanupOnError() {

    return false;
  }

  @Override
  public boolean isCopyToOtherRegions() {

    return false;
  }

  @Override
  public boolean isForce() {

    return false;
  }

  @Override
  public boolean isOverwrite() {

    return false;
  }

  @Override
  public boolean isParallel() {

    return false;
  }

  @Override
  public boolean isSkip() {

    return false;
  }

  @Override
  public boolean isSkipActualPackerRun() {

    return false;
  }

  @Override
  public boolean isSkipIfEmpty() {

    return false;
  }

  @Override
  public boolean isUnpackByDefault() {

    return false;
  }

  @Override
  public ImageBuildMojoExecutor setAdditionalEnvironment(final Map<String, String> additionalEnvironment) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setIBRArtifactData(final List<IBArchive> ibrArtifactData) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setIBRHandler(final String ibrHandler) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setAuthConfig(final IBAuthConfigBean authConfig) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setBaseAuthentications(final List<DefaultIBAuthentication> baseAuthentications) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setClassifier(final String classifier) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setCleanupOnError(final boolean cleanupOnError) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setCoordinates(final DefaultGAV coords) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setCopyToOtherRegions(final boolean copyToOtherRegions) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setDescription(final String description) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setEncoding(final String encoding) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setExcept(final String except) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setFinalName(final String finalName) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setImage(final PackerImageBuilder image) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setLog(final Logger slf4jlog) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setName(final String name) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setOnly(final String only) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setOutputDirectory(final File outputDirectory) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setOverwrite(final boolean overwrite) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setPackerArtifactData(final List<PackerManifest> packerArtifactData) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setPackerExecutable(final File packerExecutable) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setParallel(final boolean parallel) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setPlexusContainer(final PlexusContainer plexusContainer) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setProjectBuildDirectory(final File projectBuildDirectory) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setProperties(final Properties properties) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setRequirements(final List<IBRInternalDependency> requirements) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSettings(final Settings settings) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSettingsJSON(final JSONObject settingsJSON) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSkip(final boolean skip) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSkipActualPackerRun(final boolean skipActualPackerRun) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setSkipIfEmpty(final boolean skipIfEmpty) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setTimeout(final String timeout) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setTmpDir(final File tmpDir) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setVarFile(final File varFile) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setVars(final Map<String, String> vars) {

    return this;
  }

  @Override
  public ImageBuildMojoExecutor setWorkingDir(final File workingDir) {

    return this;
  }

}
