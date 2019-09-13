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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.JSON_EXTENSION;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.slf4j.Logger;

abstract public class AbstractPackerBaseObject implements ImageBaseObject {
  private GAV artifact;

  private final String id = UUID.randomUUID().toString();
  private IBAuthentication instanceAuthentication;
  private Logger log;
  private String outputFileName;

  private Optional<Path> outputPath = Optional.empty();
  private Path root;
  private Map<String, String> tags = new HashMap<>();
  private Path targetDirectory;

  @Override
  public Optional<GAV> getArtifact() {
    return Optional.ofNullable(artifact);
  }

  @Override
  public Optional<Map<String, Path>> getForcedOutput() {
    return Optional.empty();
  }

  @Override
  public PackerHintMapDAO getHintMapForType() {
    return new PackerHintMapDAO(getInstanceAuthentication(), this);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Optional<IBAuthentication> getInstanceAuthentication() {
    return Optional.ofNullable(instanceAuthentication);
  }

  @Override
  public Logger getLog() {
    return log;
  }

  @Override
  public Optional<String> getOutputFileName() {
    return Optional.ofNullable(outputFileName);
  }

  @Override
  public Optional<Path> getOutputPath() {
    return getOutputPath(Optional.empty());
  }

  @Override
  public Optional<Path> getOutputPath(final Optional<String> extension) {
    if (!outputPath.isPresent()) {
      outputPath = getTargetDirectory().map(p -> p.resolve(
          getOutputFileName().orElse(getId()) + "." + Objects.requireNonNull(extension).orElse(JSON_EXTENSION)));
    }
    return outputPath;
  }

  @Override
  public Map<String, String> getTags() {
    final Map<String, String> daTags = new HashMap<>();
    daTags.putAll(tags);
    return daTags;
  }

  @Override
  public Optional<Path> getTargetDirectory() {
    outputPath = Optional.empty();
    return Optional.ofNullable(targetDirectory);
  }

  @Override
  public Path getWorkingRootDirectory() {
    return Objects.requireNonNull(root);
  }

  @Override
  public boolean respondsTo(final String type) {
    return getNamedTypes().contains(type);
  }

  @Override
  public void setArtifact(final GAV artifact) {
    this.artifact = Objects.requireNonNull(artifact);
  }

  @Override
  public void setInstanceAuthentication(final IBAuthentication a) {
    instanceAuthentication = Objects.requireNonNull(a);
  }

  @Override
  public void setLog(final Logger log) {
    this.log = Objects.requireNonNull(log, "ATTEMPTED TO SET NULL LOG ");
  }

  @Override
  public void setOutputFileName(final String outputFileName) {
    outputPath = Optional.empty();
    this.outputFileName = Objects.requireNonNull(outputFileName);
  }

  @Override
  public void setTags(final Map<String, String> tags) {
    this.tags = Objects.requireNonNull(tags);
  }

  @Override
  public void setTargetDirectory(final Path p) {
    if (Objects.requireNonNull(p).isAbsolute())
      throw new PackerException("Absolute directories are invalid " + p.toString());
    targetDirectory = p;
  }

  @Override
  public void setWorkingRootDirectory(final Path root) {
    this.root = Objects.requireNonNull(root).toAbsolutePath().normalize();
  }

}
