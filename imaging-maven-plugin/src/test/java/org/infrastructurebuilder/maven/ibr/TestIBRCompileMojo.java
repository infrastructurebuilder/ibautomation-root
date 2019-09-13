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
package org.infrastructurebuilder.maven.ibr;

import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.infrastructurebuilder.configuration.management.IBConfigSupplier;
import org.infrastructurebuilder.configuration.management.IBRRootPathSupplier;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.DefaultIBConfigSupplier;
import org.infrastructurebuilder.configuration.management.DefaultIBRRootPathSupplier;
import org.infrastructurebuilder.configuration.management.ansible.AnsibleIBRType;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleIBRValidator;
import org.infrastructurebuilder.maven.ibr.DefaultIBRBuilderConfigElement;
import org.infrastructurebuilder.maven.ibr.IBRCompileMojo;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestIBRCompileMojo {
  @Rule
  public ExpectedException expected = ExpectedException.none();
  private List<DefaultIBRBuilderConfigElement> builders;
  private IBRCompileMojo m;
  private final WorkingPathSupplier ps = new WorkingPathSupplier();

  private Path target;
  private IBRType<JSONObject> testAnsibleCMType;

  private HashMap<String, IBRType<JSONObject>> typeMap;

  @Before
  public void setup() throws Exception {
    target = ps.getRoot();
    final Map<String, String> params = new HashMap<>();
    params.put(IBRRootPathSupplier.AUTOMATION_ROOT_PATH, target.toAbsolutePath().toString());
    params.put("file", "src/test/ansible/playbook.yml");
    final IBRRootPathSupplier rps = new DefaultIBRRootPathSupplier();
    final IBConfigSupplier cms = new DefaultIBConfigSupplier().setConfig(new HashMap<>());
    testAnsibleCMType = new AnsibleIBRType(rps, Arrays.asList(new DefaultAnsibleIBRValidator()));
    testAnsibleCMType.setConfigSupplier(cms);
    typeMap = new HashMap<>();
    typeMap.put(org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE, testAnsibleCMType);
    m = new IBRCompileMojo();
    m.setMavenProjectHelper(new DefaultMavenProjectHelper());
    m.setRootPathSupplier(rps);
    m.setMyTypes(typeMap);
    m.setPluginContext(new HashMap<String, String>());
    m.setWorkDirectory(target.resolve("ibr-tmp").toFile());
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

  @Test
  public void testExecuteWithFiles() throws MojoExecutionException, MojoFailureException {
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
    assertEquals(0,m.getIncludes().length);
    assertEquals(0,m.getExcludes().length);
    assertNotNull(m.getMavenProjectHelper());
    m.execute();
  }

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
