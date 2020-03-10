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
package org.infrastructurebuilder.ibr.utils;

import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.LoggerSupplier;
import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.config.AbstractIBRuntimeUtils;
import org.infrastructurebuilder.util.config.GAVSupplier;
import org.infrastructurebuilder.util.config.PathSupplier;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

abstract public class AbstractAutomationUtils extends AbstractIBRuntimeUtils implements AutomationUtils {

  protected AbstractAutomationUtils(PathSupplier wps, LoggerSupplier ls, GAVSupplier gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm, TypeToExtensionMapper t2em) {
    super(wps, ls, gs, cf, avm, t2em);
  }

  protected AbstractAutomationUtils(AbstractAutomationUtils ibr) {
    super(ibr);
  }

}
