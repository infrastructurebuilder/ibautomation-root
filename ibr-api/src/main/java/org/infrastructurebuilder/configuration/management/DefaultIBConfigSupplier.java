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
package org.infrastructurebuilder.configuration.management;

import java.util.Map;

import javax.inject.Named;

@Named
public class DefaultIBConfigSupplier implements IBConfigSupplier {
  private Map<String, Object> config = null;

  public DefaultIBConfigSupplier() {
  }

  @Override
  public Map<String, Object> get() {
    return config;
  }

  public IBConfigSupplier setConfig(final Map<String, Object> config) {
    this.config = config;
    return this;
  }

}
