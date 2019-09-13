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
package org.infrastructurebuilder.imaging.ibr;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.json.JSONObject;

@Named(PackerGenericIBRArchiveProvisioner.GENERIC_IBR)
@Typed(PackerProvisioner.class)
@Description("Generic Provisoner that uses an IBR as source")
public class PackerGenericIBRArchiveProvisioner extends AbstractPackerIBRProvisioner {
  public static final String GENERIC_IBR = "generic-ibr";

  @Inject
  public PackerGenericIBRArchiveProvisioner(final Map<String, IBRType> cmTypes) {
    super(cmTypes);
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject();
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(GENERIC_IBR);
  }

  @Override
  public String getPackerType() {
    return "Combat Boots";
  }

}
