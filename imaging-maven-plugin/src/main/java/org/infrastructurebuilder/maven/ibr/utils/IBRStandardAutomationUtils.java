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
package org.infrastructurebuilder.maven.ibr.utils;

import static org.infrastructurebuilder.IBConstants.DEFAULT;
import static org.infrastructurebuilder.IBConstants.MAVEN;
import static org.infrastructurebuilder.configuration.management.IBRConstants.IBR_WORKING_PATH_SUPPLIER;
import static org.infrastructurebuilder.util.InjectedSLF4JFromMavenLoggerSupplier.LOG;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.ibr.utils.AbstractAutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.IBRWorkingPathSupplier;
import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.LoggerSupplier;
import org.infrastructurebuilder.util.MavenProjectSupplier;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.DependenciesSupplier;
import org.infrastructurebuilder.util.config.GAVSupplier;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

@Named(AutomationUtils.DEFAULT_NAME)
@Singleton
public class IBRStandardAutomationUtils extends AbstractAutomationUtils {

  private final DependenciesSupplier ds;
  private final MavenProjectSupplier mpSupplier;
  private final ConfigMapSupplier    cms;

  @Inject
  public IBRStandardAutomationUtils(
      // WPS from maven run
      @Named(IBR_WORKING_PATH_SUPPLIER) IBRWorkingPathSupplier wps
      // Maven logger as an SLF4J logger
      , @Named(LOG) LoggerSupplier ls
      // GAV from the Maven project
      , @Named(MAVEN) GAVSupplier gs
      // Default (all weighted creds suppliers) credentials factory
      , @Named(DEFAULT) CredentialsFactory cf
      // Dpe Supplier
      , @Named(MAVEN) DependenciesSupplier ds
      // Version mappers
      , @Named(DEFAULT) IBArtifactVersionMapper avm
      // t2em
      , TypeToExtensionMapper t2em
      // Supplier for local maven project
      , @Named(MAVEN) MavenProjectSupplier mps) {
    super(wps, ls, gs, cf, avm, t2em);
    this.ds = Objects.requireNonNull(ds);
    this.mpSupplier = Objects.requireNonNull(mps);
    this.cms = null;
  }

  private IBRStandardAutomationUtils(IBRStandardAutomationUtils ibr, ConfigMapSupplier cms) {
    super(ibr);
    this.ds = ibr.ds;
    this.mpSupplier = ibr.mpSupplier;
    this.cms = cms;
  }

  @Override
  public List<GAV> getDependencies() {
    return ds.get();
  }

  @Override
  public AutomationUtils configure(ConfigMapSupplier cms) {
    return new IBRStandardAutomationUtils(this, cms);
  }
}
