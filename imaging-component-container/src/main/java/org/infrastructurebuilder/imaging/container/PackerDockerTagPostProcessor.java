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

import static org.infrastructurebuilder.imaging.container.DockerV1Constants.DOCKER_TAG;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.PackerPostProcessor;

@Named("packer-docker-tag-post-processor")
@Typed(PackerPostProcessor.class)
public class PackerDockerTagPostProcessor extends PackerDockerImportPostProcessor {
  public PackerDockerTagPostProcessor() {
    super(DOCKER_TAG);
  }
}
