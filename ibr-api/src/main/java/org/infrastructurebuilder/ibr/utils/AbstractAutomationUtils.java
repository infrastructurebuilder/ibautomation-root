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
package org.infrastructurebuilder.ibr.utils;

import java.util.List;
import java.util.Objects;

import org.infrastructurebuilder.util.config.AbstractIBRuntimeUtils;
import org.infrastructurebuilder.util.config.DependenciesSupplier;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.GAVSupplier;
import org.infrastructurebuilder.util.core.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.core.LoggerSupplier;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.credentials.basic.CredentialsFactory;

abstract public class AbstractAutomationUtils extends AbstractIBRuntimeUtils implements AutomationUtils {

  private final DependenciesSupplier ds;

  protected AbstractAutomationUtils(PathSupplier wps, LoggerSupplier ls, GAVSupplier gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm, TypeToExtensionMapper t2em, DependenciesSupplier ds) {
    super(wps, ls, gs, cf, avm, t2em);
    this.ds = Objects.requireNonNull(ds);
  }

  protected AbstractAutomationUtils(AbstractAutomationUtils ibr) {
    super(ibr);
    this.ds = ibr.ds;
  }

  @Override
  public List<GAV> getDependencies() {
    return ds.get();
  }


}
