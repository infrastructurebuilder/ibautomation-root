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
package org.infrastructurebuilder.imaging.container;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.DEFAULT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.imaging.AbstractPackerPostProcessor;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;

public abstract class AbstractDockerPostProcessor extends AbstractPackerPostProcessor {

  private final static String savePath = UUID.randomUUID().toString() + ".tar";
  private final Optional<String> localType;

  public AbstractDockerPostProcessor(final String localType) {
    super();
    this.localType = Optional.of(localType);
  }

  public JSONBuilder asJSONBuilder() {
    return JSONBuilder.newInstance()

        .addString(TYPE, getPackerType());
  }

  @Override
  public Optional<String> getLookupHint() {
    return localType;
  }

  @Override
  public List<String> getNamedTypes() {
    return PackerContainerBuilder.namedTypes;
  }

  @Override
  public String getPackerType() {
    return localType.orElse(DEFAULT);
  }

  public Optional<Path> getSaveTarget() {
    return getTargetDirectory().map(p -> p.resolve(savePath));
  }

  Optional<String> getRepository() {
    return getArtifact().map(a -> a.getGroupId() + "/" + a.getArtifactId());

  }

  Optional<String> getTag() {
    return getArtifact().map(a -> a.getVersion().get());
  }

}