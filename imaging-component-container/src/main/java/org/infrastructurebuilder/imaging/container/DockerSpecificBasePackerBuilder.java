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

import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.ImageData;

@Named(DockerSpecificBasePackerBuilder.DOCKER_SPECIFIC_BASE_LINUX2)
@Typed(ImageData.class)
public class DockerSpecificBasePackerBuilder extends PackerContainerBuilder {
  public static final String CENTOS7_IMAGE = "centos7";
  public static final String DOCKER_SPECIFIC_BASE_LINUX2 = "docker-base-linux2";

  public DockerSpecificBasePackerBuilder() {
    setSourceImage(CENTOS7_IMAGE);
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(DOCKER_SPECIFIC_BASE_LINUX2);
  }
}
