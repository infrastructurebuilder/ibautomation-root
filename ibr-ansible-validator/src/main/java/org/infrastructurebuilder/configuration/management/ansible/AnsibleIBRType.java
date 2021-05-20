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
package org.infrastructurebuilder.configuration.management.ansible;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE;
import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.AWS_PROVISIONING_USER;
import static org.infrastructurebuilder.ibr.IBRConstants.AMAZONEBS;
import static org.infrastructurebuilder.ibr.IBRConstants.PLAYBOOK_FILE;
import static org.infrastructurebuilder.ibr.IBRConstants.TYPE;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.AbstractIBRType;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerConstantsV1;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONObject;

@Named(ANSIBLE)
@Typed(IBRType.class)
public class AnsibleIBRType extends AbstractIBRType {

  @Inject
  public AnsibleIBRType(@Named(AutomationUtils.DEFAULT_NAME) final AutomationUtils rps,
      final List<IBRValidator> validators) {
    super(rps, validators);
    setName(ANSIBLE);
  }

  @Override
  public JSONObject transformToProvisionerEntry(final String typeName, final Path root, final Path targetFile,
      final Optional<IBArchive> archive, final List<ImageData> builders) {

    final JSONObject j = JSONBuilder.newInstance(ofNullable(getRoot()))

        .addString(TYPE, getName())

        .addPath(PLAYBOOK_FILE, requireNonNull(targetFile))

        .asJSON();
    getOverridesFor(builders).ifPresent(o -> j.put(PackerConstantsV1.OVERRIDES, o));
    return j;
  }

  private Optional<JSONObject> getOverridesFor(final List<ImageData> builders) {

    final JSONObject j = new JSONObject();
    for (final ImageData b : requireNonNull(builders)) {
      switch (b.getPackerType()) {
        case AMAZONEBS:
          if (b.getProvisioningUser().isPresent()) {
            j.put(b.getBuildExecutionName(),
                new JSONObject().put(AWS_PROVISIONING_USER, b.getProvisioningUser().get()));
          }
          break;
        default:
          break;
      }
    }
    return ofNullable(j.keySet().size() > 0 ? j : null);
  }

}
