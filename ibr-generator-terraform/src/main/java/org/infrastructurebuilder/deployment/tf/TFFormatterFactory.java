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
package org.infrastructurebuilder.deployment.tf;

import java.util.Optional;

/**
 * Terraform Formatter factory
 *
 * @author mykel.alvis
 *
 */
public interface TFFormatterFactory {

  default TFFormatter getSpecificFormatterFor(TFObject<?> resource) {
    return this.getSpecificFormatterFor(Optional.empty(), resource);
  }

  /**
   * Get a formatter for a given parent and resource.
   * @param parent
   * @param resource
   * @return
   */
  TFFormatter getSpecificFormatterFor(Optional<TFFormatter> parent, TFObject<?> resource);

}
