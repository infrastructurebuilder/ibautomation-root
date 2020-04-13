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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import org.infrastructurebuilder.automation.IBRAutomationException;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;

abstract public class AbstractIBRDialectSupplier implements IBRDialectSupplier {

  private final AutomationUtils   ibr;
  private final String            dialect;
  private final ConfigMapSupplier config;
  private final int               weight;

  protected AbstractIBRDialectSupplier(AutomationUtils ibr, String dialect) {
    this(ibr, dialect, 0);
  }

  protected AbstractIBRDialectSupplier(AutomationUtils ibr, String dialect, int weight) {
    this(ibr, null, dialect, weight);
  }

  protected AbstractIBRDialectSupplier(AutomationUtils ibr2, ConfigMapSupplier cms, String dialect2, int weight) {
    this.ibr = requireNonNull(ibr2);
    this.config = cms;
    this.dialect = requireNonNull(dialect2);
    this.weight = weight;
  }

  @Override
  public String getDialect() {
    return this.dialect;
  }

  @Override
  public ConfigMapSupplier getConfig() {
    return ofNullable(this.config).orElseThrow(() -> new IBRAutomationException("Unconfigured supplier"));
  }

  @Override
  public Integer getWeight() {
    return this.weight;
  }

  @Override
  public AutomationUtils getAutomationUtils() {
    return this.ibr;
  }
}
