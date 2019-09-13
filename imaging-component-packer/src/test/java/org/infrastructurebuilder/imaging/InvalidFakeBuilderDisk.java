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

import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.json.JSONObject;

@Named(InvalidFakeBuilderDisk.FAKE)
@Typed(ImageDataDisk.class)
public class InvalidFakeBuilderDisk extends AbstractPackerBuilderDisk implements ImageDataDisk {

  public static final String FAKE = "fake";
  private final List<String> namedTypes;

  public InvalidFakeBuilderDisk(final List<String> named) {
    namedTypes = named;
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject();
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(InvalidFakeBuilderDisk.FAKE);
  }

  @Override
  public List<String> getNamedTypes() {
    return namedTypes;
  }

  @Override
  public String getType() {
    return namedTypes.get(0);
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public void validate() {

  }
}
