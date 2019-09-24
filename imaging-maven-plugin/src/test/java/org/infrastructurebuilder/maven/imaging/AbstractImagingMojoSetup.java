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

import java.io.File;
import java.nio.charset.StandardCharsets;
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
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducer;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.infrastructurebuilder.util.auth.DummyNOPWorkingPathSupplier;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducer;
import org.infrastructurebuilder.util.config.PathSupplier;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.joor.Reflect;
import org.junit.After;
import org.junit.Before;

public class AbstractImagingMojoSetup {

  private static ContainerConfiguration dpcreq;
  private static ClassWorld kw;
  private static final String TESTING = "testing";
  private Artifact a1;
  private Artifact a2;
  private IBAuthConfigBean acb;
  private PlexusContainer container;
  private PackerImageBuilder image;
  private PathSupplier ps = new WorkingPathSupplier();
  private IBRInternalDependency r1;
  private IBRInternalDependency r2;
  private IBRInternalDependency r3;
  private IBRInternalDependency r4;
  private List<IBRInternalDependency> requirements;
  private Set<Artifact> resolvedArtifacts;
  private Type type;
  private List<IBAuthenticationProducer> writers;
  protected MojoExecution exec;
  protected ImageBuildMojo m;
  protected Path target;
  protected Path test;

  @Before
  public void setUp() throws Exception {
    ps = new DummyNOPWorkingPathSupplier();
    test = ps.get();
    target = test.getParent();
    m = new ImageBuildMojo();
    m.varFile = new File(".");
    m.additionalEnvironment = new HashMap<>();
    m.executor = new FakePackerBuildMojoExecutor();
    m.mavenProjectHelper = new DummyMavenProjectHelper();
    m.encoding = StandardCharsets.UTF_8.name();
    m.overwrite = true;
    Reflect.on(m).set("projectBuildDirectory", target.toAbsolutePath().toFile());

    Reflect.on(m).set("plexusContainer", new DefaultPlexusContainer());
    m.skipActualPackerRun = true;
    m.workingDir = test.toFile();

    final Settings settings = new Settings();
    final Server server = new Server();
    server.setId("1");
    server.setPassword("P");
    server.setUsername("U");
    settings.addServer(server);
    m.settings = settings;
    writers = Arrays.asList(new DummyNOPAuthenticationProducer());
    m.classifier = null;
    m.baseAuthentications = Collections.emptyList();
    m.additionalEnvironment = Collections.emptyMap();

    final MavenProject p = new MavenProject();
    p.setName("projectName");
    r1 = new IBRInternalDependency();
    r1.setGroupId("junit");
    r1.setArtifactId("junit");
    r1.setUnpack(false);

    r2 = new IBRInternalDependency();
    r2.setGroupId("junit");
    r2.setArtifactId("junit");
    r2.setUnpack(true);
    requirements = Arrays.asList(r1, r2);
    final DefaultArtifactHandler handler = new DefaultArtifactHandler();
    a1 = new DefaultArtifact("junit", "junit", "4.12", "runtime", "jar", null, handler);
    a1.setFile(target.resolve("test-classes").resolve("junit-4.12.jar").toFile());
    a2 = new DefaultArtifact("org.infrastructurebuilder.infrastructure", "base-image-test", "0.2.0-SNAPSHOT", "runtime", "packer",
        null, handler);
    a2.setFile(target.resolve("test-classes").resolve("manifest-packer.json").toFile());
    resolvedArtifacts = new HashSet<>(Arrays.asList(a1, a2));
    p.setArtifacts(resolvedArtifacts);
    p.setGroupId("X");
    p.setArtifactId("Y");
    p.setPackaging("packer");
    p.setVersion("1.0.0");
    m.description = "describe me";
    m.requirements = requirements;
    exec = new MojoExecution(null, "abc");
    Reflect.on(m).set("mojo", exec);
    m.setLog(new DefaultLog(new ConsoleLogger()));
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
    final DummyNOPAuthenticationProducerFactory authFactory = new DummyNOPAuthenticationProducerFactory(ps);
    acb = new IBAuthConfigBean();
    acb.setTempDirectory(test.toFile());
    final DefaultIBAuthentication singleAuth = new DefaultIBAuthentication();
    singleAuth.setServerId("1");
    singleAuth.setType("amazon-ebs");
    singleAuth.setTarget("us-west-2");
    acb.setAuths(Arrays.asList(singleAuth));
    m.authConfig = acb;
    m.image = image;
    final Server s2 = new Server();
    s2.setId("1");
    s2.setUsername("x");
    s2.setPassword("Y");
    final Settings z = new Settings();
    z.addServer(s2);
    m.settings = z;
    m.tmpDir = test.resolve(UUID.randomUUID().toString()).toFile();
    final Artifact art = new DefaultArtifact("X", "Y", "1.0.0", "runtime", "packer", null, handler);
    p.setArtifact(art);
    m.project = p;
    m.outputDirectory = test.toFile();
    m.finalName = "jeff";
  }

  @After
  public void teardown() throws Exception {
    if (test != null) {
      IBUtils.deletePath(test);
    }
  }

}