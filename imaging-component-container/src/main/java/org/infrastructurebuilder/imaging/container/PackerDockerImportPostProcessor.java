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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.REPOSITORY;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TAG;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.DOCKER_IMPORT;
import static org.infrastructurebuilder.util.config.IncrementingDatedWorkingPathSupplier.INCREMENTING_DATED_WPS;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.PackerPostProcessor;
import org.infrastructurebuilder.util.config.PathSupplier;
import org.json.JSONObject;

@Named(DOCKER_IMPORT)
@Typed(PackerPostProcessor.class)
public class PackerDockerImportPostProcessor extends AbstractDockerPostProcessor {

  @Inject
  public PackerDockerImportPostProcessor(@Named(INCREMENTING_DATED_WPS) PathSupplier ps) {
    this(DOCKER_IMPORT);
  }

  protected PackerDockerImportPostProcessor(final String localType) {
    super(localType);
  }

  @Override
  public JSONObject asJSON() {

    return super.asJSONBuilder()

        .addString(REPOSITORY, getRepository())

        .addString(TAG, getTag())

        .asJSON();

  }

  @Override
  public void validate() {
    getArtifact().flatMap(c -> c.getVersion())
        .orElseThrow(() -> new PackerException("Artifact does not contain a valid version"));
  }
}
