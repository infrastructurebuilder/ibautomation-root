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

import java.lang.System.Logger;
import java.util.Collections;
import java.util.List;

import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.FakeGAVSupplier;
import org.infrastructurebuilder.util.config.FakeIBVersionsSupplier;
import org.infrastructurebuilder.util.config.FakeTypeToExtensionMapper;
import org.infrastructurebuilder.util.config.factory.FakeCredentialsFactory;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.credentials.basic.CredentialsFactory;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;

public class AutomationUtilsTesting extends AbstractAutomationUtils {

  private final static Logger log = System.getLogger(AutomationUtilsTesting.class.getName());

  public AutomationUtilsTesting(PathSupplier wps, Logger ls, GAV gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm) {
    super(wps, () -> ls, new FakeGAVSupplier(gs), cf, avm, new FakeTypeToExtensionMapper(),
        () -> Collections.emptyList());
  }

  public AutomationUtilsTesting(PathSupplier wps, Logger log) {
    this(wps, log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
          @Override
          public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
            return Collections.emptyList();
          }
        });
  }

  public AutomationUtilsTesting(Logger log) {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
          @Override
          public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
            return Collections.emptyList();
          }
        });
  }

  public AutomationUtilsTesting() {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
          @Override
          public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
            return Collections.emptyList();
          }
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
