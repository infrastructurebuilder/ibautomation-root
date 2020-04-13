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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.OVERRIDES;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;

abstract public class AbstractPackerProvisioner extends AbstractPackerBaseObject implements PackerProvisioner {
  private List<ImageData>               builders;
  private final Map<String, JSONObject> overrides = new HashMap<>();

  public AbstractPackerProvisioner() {
    super();
  }

  @Override
  public Class<?> getLookupClass() {
    return PackerProvisioner.class;
  }

  @Override
  public Optional<JSONObject> getOverrides() {
    return overrides.size() == 0 ? Optional.empty()
        : Optional.of(new JSONObject().put(OVERRIDES, new JSONObject(overrides)));
  }

  @Override
  public void setBuilders(final List<ImageData> builders) {
    this.builders = Objects.requireNonNull(builders);
  }

  @Override
  public PackerProvisioner updateWithOverrides(final List<ImageData> builders) {
    Objects.requireNonNull(builders).stream().forEach(b -> {
      addOverrideForType(b).ifPresent(o -> overrides.put(b.getId(), o));
    });
    return this;
  }

  protected Optional<JSONObject> addOverrideForType(final ImageData b) {
    getLog().debug("No override for builder " + b.getId() + " in " + getId());
    return Optional.empty();
  }

  protected List<ImageData> getBuilders() {
    return builders;
  }

}
