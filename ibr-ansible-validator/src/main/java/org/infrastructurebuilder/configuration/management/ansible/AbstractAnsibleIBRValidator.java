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
package org.infrastructurebuilder.configuration.management.ansible;

import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE;

import java.util.Objects;

import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.json.JSONObject;

public abstract class AbstractAnsibleIBRValidator implements IBRValidator<JSONObject> {

  @Override
  public boolean respondsTo(final IBRType<JSONObject> type) {
    return ANSIBLE.equals(Objects.requireNonNull(type).getName());
  }

}
