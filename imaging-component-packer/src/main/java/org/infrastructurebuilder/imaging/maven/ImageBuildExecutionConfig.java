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
package org.infrastructurebuilder.imaging.maven;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.json.JSONObject;

public interface ImageBuildExecutionConfig {

  Map<String, String> getAdditionalEnvironment();

  PrintStream getAdditionalPrintStream();

  List<IBArchive> getIBRArchives();

  String getIBRHandler();

  Map<IBArchive, IBRInternalDependency> getIBRMapping();

  ArchiverManager getArchiverManager();

  IBAuthConfigBean getAuthFileWriter();

  List<DefaultIBAuthentication> getBaseAuthentications();

  String getClassifier();

  GAV getCoordinates();

  String getDescription();

  Charset getEncoding();

  String getExcept();

  String getFinalName();

  PackerImageBuilder getImage();

  String getName();

  String getOnly();

  Path getOutputDirectory();

  Path getPackerExecutable();

  List<PackerManifest> getPackerManifests();

  PlexusContainer getPlexusContainer();

  File getProjectBuildDirectory();

  Properties getProperties();

  List<IBRInternalDependency> getRequirements();

  JSONObject getSettingsJSON();

  String getTimeout();

  Path getTmpDir();

  File getVarFile();

  @Deprecated
  Map<String, String> getVars();

  Path getWorkingDirectory();

  boolean isCleanupOnError();

  boolean isCopyToOtherRegions();

  boolean isForce();

  boolean isOverwrite();

  boolean isParallel();

  boolean isSkip();

  boolean isSkipActualPackerRun();

  boolean isSkipIfEmpty();

  @Deprecated
  boolean isUnpackByDefault();
}