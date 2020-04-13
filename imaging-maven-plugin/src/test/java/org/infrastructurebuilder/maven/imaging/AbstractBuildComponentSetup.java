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
package org.infrastructurebuilder.maven.imaging;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.maven.MockedPackerBean;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.joor.Reflect;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractBuildComponentSetup {
  public final static TestingPathSupplier ps  = new TestingPathSupplier();
  public final static Logger              log = LoggerFactory.getLogger(AbstractBuildComponentSetup.class);
  public final static AutomationUtils     ibr = new AutomationUtilsTesting(ps, log);

  protected static ClassWorld   kw;
  protected static final String TESTING = "testing";

  private Artifact                        a1;
  private Artifact                        a2;
  private IBAuthConfigBean                acb;
  private ArtifactHandler                 handler;
  private PackerImageBuilder              image;
  private IBRInternalDependency           r1;
  private IBRInternalDependency           r2;
  private List<IBRInternalDependency>     requirements;
  private Type                            type;
  protected DefaultImageBuildMojoExecutor m;
  protected Set<Artifact>                 resolvedArtifacts;
  protected Path                          target;
  protected Path                          test;
  protected PlexusContainer               container;

  @BeforeClass
  public static void beforeClass() {

  }

  @Before
  public void setUp() throws Exception {

    handler = new DefaultArtifactHandler();
    test = ps.get();
    target = ps.getRoot();

    m = new DefaultImageBuildMojoExecutor(ibr, new FakeArchiverManager());
    final MockedPackerBean b = new MockedPackerBean("defaultPasWithNothing", null);
    Reflect.on(m).set("data", b);

    m.setLog(log);

    m.setEncoding(UTF_8.name());
    m.setOverwrite(true);
    Reflect.on(m).set("projectBuildDirectory", target.toAbsolutePath().toFile());
    m.setName(UUID.randomUUID().toString());
    Reflect.on(m).set("plexusContainer", container);
    m.setSkipActualPackerRun(true);
    m.setWorkingDir(test.toFile());
    m.setProperties(System.getProperties());
    m.setCoordinates(new DefaultGAV("x:y:1.0.0:jar"));

    final Map<String, String> vars = new HashMap<>();
    m.setSkip(false).setTimeout("PT30M").setVars(vars);

    final Settings settings = new Settings();
    final Server server = new Server();
    server.setId("1");
    server.setPassword("P");
    server.setUsername("U");
    settings.addServer(server);
    m.setSettings(settings);
    m.setClassifier(null);
    m.setBaseAuthentications(Collections.emptyList());
    m.setAdditionalEnvironment(Collections.emptyMap());

    r1 = new IBRInternalDependency();
    r1.setGroupId("junit");
    r1.setArtifactId("junit");
    r1.setType("jar");
    r1.setUnpack(false);

    r2 = new IBRInternalDependency();
    r2.setGroupId("junit");
    r2.setArtifactId("junit");
    r2.setType("jar");
    r2.setUnpack(true);
    requirements = Arrays.asList(r1, r2);

    a1 = new DefaultArtifact("junit", "junit", "4.12", "runtime", "jar", null, handler);
    a1.setFile(target.resolve("test-classes").resolve("junit-4.12.jar").toFile());
    a2 = new DefaultArtifact("org.infrastructurebuilder.infrastructure", "base-image-test", "0.2.0-SNAPSHOT", "runtime",
        "packer", "def", handler);
    a2.setFile(target.resolve("test-classes").resolve("manifest-packer.json").toFile());
    resolvedArtifacts = new HashSet<>(Arrays.asList(a1, a2));

    m.setDescription("describe me");
    m.setRequirements(requirements);

    image = new PackerImageBuilder();
    final List<Type> typeMap = new ArrayList<>();
    type = new Type();
    type.setHint("specific-amazonebs");
    typeMap.add(type);
    image.setTypes(typeMap);
    final Map<String, String> tags = new HashMap<>();
    tags.put("CostCenter", "LABS");
    tags.put("Platform", "Linux");
    image.setTags(tags);

    acb = new IBAuthConfigBean();
    acb.setTempDirectory(test.toFile());
    final DefaultIBAuthentication singleAuth = new DefaultIBAuthentication();
    singleAuth.setServerId("1");
    singleAuth.setType("amazon-ebs");
    singleAuth.setTarget("us-west-2");
    acb.setAuths(Arrays.asList(singleAuth));
    m.setAuthConfig(acb);
    m.setImage(image);
    m.setSettingsJSON(new JSONObject().put("1", new JSONObject().put("username", "x").put("password", "Y")));
    m.setTmpDir(test.resolve(UUID.randomUUID().toString()).toFile());
    new DefaultArtifact("X", "Y", "1.0.0", "runtime", "packer", "whut", handler);

    m.setOutputDirectory(test.toFile());
    m.setFinalName("jeff");
  }

  @After
  public void teardown() throws Exception {
    if (test != null) {
      IBUtils.deletePath(test);
    }
  }

}