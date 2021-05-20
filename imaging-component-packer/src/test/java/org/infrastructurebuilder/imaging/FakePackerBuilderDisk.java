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
package org.infrastructurebuilder.imaging;

import static java.util.Arrays.asList;
import static java.util.Optional.of;

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

public class FakePackerBuilderDisk extends AbstractPackerBuilderDisk {

  public static final String FAKE = "fake";

  public FakePackerBuilderDisk() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getType() {
    return FAKE;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject();
  }

  @Override
  public Optional<String> getLookupHint() {
    return of(FAKE);
  }

  @Override
  public List<String> getNamedTypes() {
    return asList(FAKE);
  }

  @Override
  public void validate() {

  }

}
