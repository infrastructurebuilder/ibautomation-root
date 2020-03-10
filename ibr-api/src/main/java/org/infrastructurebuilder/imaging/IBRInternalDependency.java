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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.configuration.management.IBArchive.IBR;

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.GAV;

public class IBRInternalDependency {
  public String groupId, artifactId, classifier, type = "ibr";
  private Path _tdir;
  private Path file = null;
  private boolean overwrite = false;
  private String remote;
  private Optional<Boolean> unpack = Optional.empty();

  public void applyTargetDir(final Path target) {
    _tdir = requireNonNull(target);
  }

  public Optional<Path> getFile() {
    return ofNullable(file);
  }

  public Optional<String> getRemote() {
    return ofNullable(remote);
  }

  public Optional<Path> getTargetDir() {
    return ofNullable(_tdir);
  }

  public boolean isOverwrite() {
    return overwrite;
  }

  public boolean isSendToRemote() {
    return getRemote().isPresent();
  }

  public Optional<Boolean> isUnpack() {
    return IBR.equals(type) ? of(true) : unpack;
  }

  public boolean matches(final GAV a) {
    if (validate()) {
      if (!groupId.equals(a.getGroupId()))
        return false;
      if (!artifactId.equals(a.getArtifactId()))
        return false;
      if (!type.equals(a.getExtension()))
        return false;
      if (classifier == null)
        return !a.getClassifier().isPresent();
      else
        return classifier.equals(a.getClassifier().orElse(null));
    }
    return false;
  }

  public void set_tdir(final Path _tdir) {
    throw new PackerException("Cannot set _tdir");
  }

  public void setArtifactId(final String artifactId) {
    this.artifactId = artifactId;
  }

  public void setClassifier(final String classifier) {
    this.classifier = classifier;
  }

  public void setFile(final Path file) {
    this.file = requireNonNull(file);
  }

  public void setGroupId(final String groupId) {
    this.groupId = groupId;
  }

  public void setOverwrite(final boolean overwrite) {
    this.overwrite = overwrite;
  }

  public void setRemote(final String sendToRemote) {
    remote = sendToRemote;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public void setUnpack(final boolean unpack) {
    this.unpack = of(Boolean.valueOf(unpack));
  }

  @Override
  public String toString() {
    return groupId + ":" + artifactId + ":" + classifier + ":" + type;
  }

  public boolean validate() {
    if (groupId == null)
      return false;
    if (artifactId == null)
      return false;
    if (type == null)
      return false;
    return true;
  }
}
