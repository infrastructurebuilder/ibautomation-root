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

import java.util.Optional;

public enum PackerTypeMapperProcessingSection {
  BUILDER("builders"), POST_PROCESSOR("post-processors"), PROVISIONER("provisioners"), VARIABLE("variables");
  public final static Optional<PackerTypeMapperProcessingSection> valueFor(final String type) {
    for (final PackerTypeMapperProcessingSection a : PackerTypeMapperProcessingSection.values())
      if (a.typeString.equals(type))
        return Optional.of(a);
    return Optional.empty();
  }

  private String typeString;

  private PackerTypeMapperProcessingSection(final String typeString) {
    this.typeString = typeString;
  }

  public String getKeyString() {
    return typeString;
  }

}
