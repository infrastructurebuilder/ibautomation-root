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
package org.infrastructurebuilder.imaging.container;

import static org.infrastructurebuilder.imaging.container.DockerV1Constants.CONTAINER;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.CONTAINER_PATH;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.HOST_PATH;

import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.imaging.AbstractPackerBuilderDisk;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONObject;

public class PackerDockerBuilderDisk extends AbstractPackerBuilderDisk {

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(HOST_PATH, getHostPath())

        .addString(CONTAINER_PATH, getContainerPath())

        .asJSON();
  }

  public Optional<String> getContainerPath() {
    return getVirtualName();
  }

  public Optional<String> getHostPath() {
    return getDeviceName();

  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(getType());
  }

  @Override
  public List<String> getNamedTypes() {
    return PackerContainerBuilder.namedTypes;
  }

  @Override
  public String getType() {
    return CONTAINER;
  }

  @Override
  public boolean isValid() {
    return getContainerPath().isPresent() && getHostPath().isPresent();
  }

  public void setContainerPath(final String containerPath) {
    setVirtualName(containerPath);
  }

  public void setHostPath(final String hostPath) {
    setDeviceName(hostPath);
  }

  @Override
  public void validate() {

  }

}
