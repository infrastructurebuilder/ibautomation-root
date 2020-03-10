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
package org.infrastructurebuilder.imaging;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.util.LoggerEnabled;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.slf4j.Logger;

public interface ImageBaseObject extends JSONOutputEnabled, LoggerEnabled {

  Optional<GAV> getArtifact();

  Optional<Map<String, Path>> getForcedOutput();

  PackerHintMapDAO getHintMapForType();

  String getId();

  Optional<IBAuthentication> getInstanceAuthentication();

  Class<?> getLookupClass();

  Optional<String> getLookupHint();

  List<String> getNamedTypes();

  Optional<String> getOutputFileName();

  Optional<Path> getOutputPath();

  Optional<Path> getOutputPath(Optional<String> extension);

  String getPackerType();

  Map<String, String> getTags();

  Optional<Path> getTargetDirectory();

  Path getWorkingRootDirectory();

  boolean respondsTo(String type);

  void setArtifact(GAV artifact);

  void setInstanceAuthentication(IBAuthentication a);

  void setLog(Logger log);

  void setOutputFileName(String outputFileName);

  void setTags(Map<String, String> tags);

  void setTargetDirectory(Path p);

  void setWorkingRootDirectory(Path root);

  void validate();

}
