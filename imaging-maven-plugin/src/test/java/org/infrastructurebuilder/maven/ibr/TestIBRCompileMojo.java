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
package org.infrastructurebuilder.maven.ibr;

import static java.util.Optional.empty;
import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.System.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DefaultMavenProjectHelper;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.infrastructurebuilder.configuration.management.DefaultIBConfigSupplier;
import org.infrastructurebuilder.configuration.management.IBConfigSupplier;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.ansible.AnsibleIBRType;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleIBRValidator;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleValidator;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.ibr.utils.IBRWorkingPathSupplier;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.executor.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestIBRCompileMojo {
  public final static Logger                   log      = System.getLogger(TestIBRCompileMojo.class.getName());
  @Rule
  public ExpectedException                     expected = ExpectedException.none();
  private List<DefaultIBRBuilderConfigElement> builders;
  private IBRCompileMojo                       m;
  private final TestingPathSupplier            ps       = new TestingPathSupplier();
  private Path                                 target;
  private IBRType                              testAnsibleCMType;

  private HashMap<String, IBRType> typeMap;
  private final static TestingPathSupplier wps  = new TestingPathSupplier();
  private VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());

  @Before
  public void setup() throws Exception {
    target = ps.getRoot();
    Path workingPath = target.resolve("ibr-tmp");

    m = new IBRCompileMojo();
    m.setMavenProjectHelper(new DefaultMavenProjectHelper());
    IBRWorkingPathSupplier i = new IBRWorkingPathSupplier();
    i.setT(workingPath);
    m.setRootPathSupplier(i);
    m.setPluginContext(new HashMap<String, String>());
    m.setWorkDirectory(workingPath.toFile());
    final AutomationUtilsTesting rps = new AutomationUtilsTesting(() -> m.getWorkDirectory(), log);
    final IBConfigSupplier cms = new DefaultIBConfigSupplier().setConfig(new HashMap<>());
    testAnsibleCMType = new AnsibleIBRType(rps,
        Arrays.asList(new DefaultAnsibleIBRValidator(rps, new DefaultAnsibleValidator(vpef))));
    testAnsibleCMType.setConfigSupplier(cms);
    typeMap = new HashMap<>();
    typeMap.put(org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE, testAnsibleCMType);
    m.setMyTypes(typeMap);
    m.setSources(Paths.get("./src/test/").toFile());
    m.setUnfilteredResourcesDirectory(Paths.get("./src/test/resources").toFile());

    builders = new ArrayList<>();
  }

  @Test
  public void testExecuteNoTypes() throws MojoExecutionException, MojoFailureException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("No such type: notARealType");
    final DefaultIBRBuilderConfigElement badBuilder = new DefaultIBRBuilderConfigElement();
    badBuilder.setTest(false);
    badBuilder.setType("notARealType");
    badBuilder.setConfig(new HashMap<String, Object>());
    builders.add(badBuilder);
    m.setBuilders(builders);
    m.execute();
  }

  @Ignore
  @Test
  public void testExecuteWithFiles() throws MojoExecutionException, MojoFailureException, InitializationException {
    final DefaultIBRBuilderConfigElement goodBuilder = new DefaultIBRBuilderConfigElement();
    goodBuilder.setTest(false);
    goodBuilder.setType(ANSIBLE);
    final Map<String, Object> config = new HashMap<>();
    config.put("file", "ansible/playbook.yml");
    goodBuilder.setConfig(config);

    final DefaultIBRBuilderConfigElement builder = new DefaultIBRBuilderConfigElement();
    builder.setTest(false);
    builder.setType(ANSIBLE);
    final Map<String, Object> config2 = new HashMap<>();
    config2.put("file", "ansible/sub/directory/playbook2.yml");
    builder.setConfig(config2);
    builder.setConfig(config);
    builders.add(goodBuilder);
    builders.add(builder);
    m.setBuilders(builders);
    assertEquals(0, m.getIncludes().length);
    assertEquals(0, m.getExcludes().length);
    assertNotNull(m.getMavenProjectHelper());
    m.initialize();
    m.execute();
  }

  @Ignore
  @Test
  public void testExecuteWithNoResources() throws MojoExecutionException, MojoFailureException {
    final DefaultIBRBuilderConfigElement goodBuilder = new DefaultIBRBuilderConfigElement();
    goodBuilder.setTest(false);
    goodBuilder.setType(org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE);
    final Map<String, Object> config = new HashMap<>();
    config.put("file", "ansible/playbook.yml");
    goodBuilder.setConfig(config);

    m.setUnfilteredResourcesDirectory(Paths.get("/non/existent").toFile());

    builders.add(goodBuilder);

    m.setBuilders(builders);
    m.execute();
  }

  @Test
  public void testNoBuildersShouldFail() throws MojoExecutionException, MojoFailureException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("No builders set");
    m.setBuilders(builders);
    m.execute();
  }

}
