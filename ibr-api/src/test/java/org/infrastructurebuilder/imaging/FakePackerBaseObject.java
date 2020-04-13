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

import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONObject;
import org.slf4j.Logger;

public class FakePackerBaseObject implements ImageBaseObject {

  private final String packerType;

  public FakePackerBaseObject(final String type) {
    packerType = type;
  }

  @Override
  public JSONObject asJSON() {

    return null;
  }

  @Override
  public Optional<GAV> getArtifact() {

    return null;
  }

  @Override
  public Optional<Map<String, Path>> getForcedOutput() {

    return null;
  }

  @Override
  public IBRHintMap getHintMapForType() {
    return null;
  }

  @Override
  public String getId() {

    return null;
  }

  @Override
  public Optional<IBAuthentication> getInstanceAuthentication() {

    return null;
  }

  @Override
  public Logger getLog() {

    return null;
  }

  @Override
  public Class<?> getLookupClass() {

    return null;
  }

  @Override
  public Optional<String> getLookupHint() {

    return null;
  }

  @Override
  public List<String> getNamedTypes() {

    return null;
  }

  @Override
  public Optional<String> getOutputFileName() {

    return null;
  }

  @Override
  public Optional<Path> getOutputPath() {

    return null;
  }

  @Override
  public Optional<Path> getOutputPath(final Optional<String> extension) {

    return null;
  }

  @Override
  public String getPackerType() {
    return packerType;
  }

  @Override
  public Map<String, String> getTags() {

    return null;
  }

  @Override
  public Optional<Path> getTargetDirectory() {

    return null;
  }

  @Override
  public Path getWorkingRootDirectory() {

    return null;
  }

  @Override
  public boolean respondsTo(final String type) {

    return false;
  }

  @Override
  public void setArtifact(final GAV artifact) {

  }

  @Override
  public void setInstanceAuthentication(final IBAuthentication a) {

  }

  @Override
  public void setLog(final Logger log) {

  }

  @Override
  public void setOutputFileName(final String outputFileName) {

  }

  @Override
  public void setTags(final Map<String, String> tags) {

  }

  @Override
  public void setTargetDirectory(final Path p) {

  }

  @Override
  public void setWorkingRootDirectory(final Path root) {

  }

  @Override
  public void validate() {

  }

}
