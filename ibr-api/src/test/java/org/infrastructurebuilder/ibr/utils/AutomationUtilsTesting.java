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

import org.infrastructurebuilder.ibr.utils.AbstractAutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.FakeCredentialsFactory;
import org.infrastructurebuilder.util.FakeTypeToExtensionMapper;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.FakeGAVSupplier;
import org.infrastructurebuilder.util.config.FakeIBVersionsSupplier;
import org.infrastructurebuilder.util.config.PathSupplier;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomationUtilsTesting extends AbstractAutomationUtils {

  private final static Logger log = LoggerFactory.getLogger(AutomationUtilsTesting.class);

  public AutomationUtilsTesting(PathSupplier wps, Logger ls, GAV gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm) {
    super(wps, () -> ls, new FakeGAVSupplier(gs), cf, avm, new FakeTypeToExtensionMapper());
  }

  public AutomationUtilsTesting(PathSupplier wps, Logger log) {
    this(wps, log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
        });
  }

  public AutomationUtilsTesting(Logger log) {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
        });
  }

  public AutomationUtilsTesting() {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
        });
  }

  public Logger getLog() {
    return log;
  }

  public TestingPathSupplier getTestingPathSupplier() {
    return (TestingPathSupplier) ((super.wps instanceof TestingPathSupplier) ? super.wps : null);
  }

  @Override
  public AutomationUtils configure(ConfigMapSupplier cms) {
    return this;
  }

}
