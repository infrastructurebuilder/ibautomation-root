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
package org.infrastructurebuilder.imaging.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.infrastructurebuilder.imaging.FakePackerExecutionConfig;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducer;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducer;
import org.joor.Reflect;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackerBeanTest {
  private static final String DESCRIPTION = "Description";

  private static ContainerConfiguration dpcreq;

  private static ClassWorld kw;

  private final static Logger log = LoggerFactory.getLogger(PackerBeanTest.class);

  private static final String TESTING = "TESTING";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    final String mavenCoreRealmId = TESTING;
    kw = new ClassWorld(mavenCoreRealmId, PackerBeanTest.class.getClassLoader());
    dpcreq = new DefaultContainerConfiguration().setClassWorld(kw).setClassPathScanning(PlexusConstants.SCANNING_INDEX)
        .setName(TESTING);

  }

  private IBAuthConfigBean authConfig;
  private Path authTestPath;
  private Path errorpacker;
  private Path errorpacker2;

  private DummyNOPAuthenticationProducerFactory f;

  private PackerImageBuilder image;

  private PackerBean med;

  private Path packer;

  private Path targetPath;

  private Path temp;

  private Type type;

  @Before
  public void setUp() throws Exception {

    final String target = Optional.ofNullable(System.getProperty("target")).orElse("./target");
    targetPath = Paths.get(target).toRealPath().toAbsolutePath();
    temp = targetPath.resolve(UUID.randomUUID().toString());
    authTestPath = targetPath.resolve("test-auth");
    packer = targetPath.resolve("packer");
    packer.toFile().setExecutable(true);
    Files.createDirectories(authTestPath);
    med = new PackerBean();
    final Path p = authTestPath.resolve(UUID.randomUUID().toString());
    Files.createDirectories(p);
    final FakePackerExecutionConfig fake = new FakePackerExecutionConfig(targetPath, p, temp, packer, kw, dpcreq);
    med.setExecutionConfigFrom(fake);
    errorpacker = targetPath.resolve("test-classes").resolve("fakepackererror.sh");
    errorpacker.toFile().setExecutable(true);
    errorpacker2 = targetPath.resolve("test-classes").resolve("fakepackererror2.sh");
    errorpacker2.toFile().setExecutable(true);

    final IBAuthenticationProducer producer = new DummyNOPAuthenticationProducer();
    f = new DummyNOPAuthenticationProducerFactory(() -> temp, Arrays.asList(producer));
    f.setTemp(authTestPath.resolve(UUID.randomUUID().toString()));
    authConfig = new IBAuthConfigBean();
    authConfig.setTempDirectory(authTestPath.resolve(UUID.randomUUID().toString()).toFile());

    image = new PackerImageBuilder();
    final List<Type> m = new ArrayList<>();
    type = new Type();
    type.setHint("specific-amazonebs");
    m.add(type);
    image.setTypes(m);
    final Map<String, String> tags = new HashMap<>();
    tags.put("CostCenter", "LABS");
    tags.put("Platform", "Linux");
    image.setTags(tags);
    final DefaultIBAuthentication singleAuth = new DefaultIBAuthentication();
    singleAuth.setServerId("1");
    singleAuth.setType("amazon-ebs");
    singleAuth.setTarget("us-west-2");
    authConfig.setAuths(Arrays.asList(singleAuth));

  }

  @Test
  public void testCoordsetc() {
    med.setCoords(new DefaultGAV("A", "B", "1.0.0"));
    assertEquals(StandardCharsets.UTF_8, med.getEncoding());
    assertFalse(med.getExcept().isPresent());
    med.setExcept("A");
    assertTrue(med.getExcept().isPresent());
    med.setFinalName("B");
    assertFalse(med.getVarFile().isPresent());
    med.setVarFile(targetPath.toFile());
    assertTrue(med.getVarFile().isPresent());
  }

  @Test(expected = PackerException.class)
  public void testEmptyPlexusContainer() {
    med = new PackerBean();
    med.getPlexusContainer();
    fail("Should not be here");
  }

  @Test
  public void testExecutePacker1() throws Exception {
    med.setSkipActualPackerRun(true);
    med.setPackerExecutable(packer);
    med.setLog(log);
    Optional<Map<String, Path>> vv;
    med.setCleanupOnError(false);
    med.setSkipActualPackerRun(true);
    vv = med.executePacker("default");
    assertFalse(vv.isPresent());
  }

  @Test
  public void testFailNull() {
    PackerBean.failNull("X", "Y");
  }

  @Test(expected = PackerException.class)
  public void testFailNull2() {
    PackerBean.failNull(null, "Z");
  }

  @Test(expected = PackerException.class)
  public void testForceCreateDir() throws IOException {
    final Path p = targetPath.resolve(UUID.randomUUID().toString());
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.addAll(Arrays.asList(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ));

    Files.createDirectories(p, PosixFilePermissions.asFileAttribute(perms));
    PackerBean.forceCreateDir.apply(p);
  }

  @Test
  public void testGens() {
    assertFalse(med.getGenerator().isPresent());
    med.setGenerator(null);
    assertFalse(med.getGenerator().isPresent());
    med.setGenerator(() -> {
      return null;
    });
    assertTrue(med.getGenerator().isPresent());
    assertNull(med.getGenerator().get().get());
  }

  @Test
  public void testGetAM() {
    assertNotNull(med.getArchiverManager());
  }

  @Test
  public void testGetDesc() {
    final String desc = Reflect.on(med).get("description");
    assertEquals(PackerBeanTest.DESCRIPTION, desc);
  }

  @Test
  public void testGetHandler() {
    assertNotNull(med.getIBRHandler());
  }

  @Test
  public void testGetName() {
    final String name = Reflect.on(med).get("name");
    assertEquals("name", name);
  }

  @Test
  public void testGetPackerFactory() {
  }

  @Test
  public void testGetPackerFActoryTwice() throws PackerException, ComponentLookupException {
    final PackerFactory<JSONObject> a = med.getPackerFactory();
    final PackerFactory<JSONObject> b = med.getPackerFactory();
    assertTrue(a == b);
  }

  @Test
  public void testGetParams1() {
    log.info("Parameters -> " + med.getParams());
    assertEquals(4, med.getParams().size());
  }

  @Test
  public void testGetProperties() {
    final Properties p = Reflect.on(med).get("properties");
    assertNotNull(p);
  }

  @Test
  public void testGetruntimeTwice() {
    final Map<String, String> a = med.getRuntimeEnvironment();
    final Map<String, String> b = med.getRuntimeEnvironment();
    assertTrue(a == b);
  }

  @Test
  public void testGetSet() {
    med.setEncoding(StandardCharsets.UTF_8);
    med.setExcept(null);
    med.setOnly(null);
    med.setParallel(false);
    med.setVars(Collections.emptyMap());
  }

  @Test
  public void testGetTemp() {
    final Path p = Reflect.on(med).get("tempDirectory");
    assertNotNull(p);
  }

  @Test
  public void testGetTimeout() {
    med.setTimeout("PT1S");
    assertEquals(Duration.ofSeconds(1), med.getTimeout().get());
  }

  @Test
  public void testMavenExternalizedData() {
    assertNotNull(med);
  }

  @Test
  public void testRequirements() {
    med = new PackerBean();
    assertFalse(med.getRequirements().isPresent());
    med.setRequirements(Collections.emptyList());
    assertTrue(med.getRequirements().isPresent());
  }

  @Test
  public void testSetAuthConfig() throws ComponentLookupException {
    med.setLog(log);
    med.setAdditionalEnvironment(new HashMap<>());
    assertNotNull(med.getAdditionalEnvironment());
    final Map<String, String> mm = med.getRuntimeEnvironment();
    assertTrue(mm.size() > 2);

  }

  @Test
  public void testSetClassifier() {
    med.setClassifier("A");
    assertEquals("A", med.getClassifier().get());
    med.setClassifier("");
    assertFalse(med.getClassifier().isPresent());
  }

  @Test
  public void testSetPackerExecutable() {
    final Path tPath = targetPath.resolve("packer");
    med.setPackerExecutable(tPath);
    final Path actual = Reflect.on(med).get("packerExecutable");
    assertEquals(tPath, actual);
  }

  @Test
  public void testSetProjectBuildDirectory() {
    med.setProjectBuildDirectory(targetPath.toFile());
    assertEquals(targetPath.toFile(), med.getProjectBuildDirectory().get().toFile());
  }

  @Test(expected = PackerException.class)
  public void testValidate1() {
    med.validate("executionId");
  }

  @Test
  public void testWriteJSON() throws JSONException, IOException {
    final JSONObject j = new JSONObject().put("X", "Y");
    final Path pp = med.writeJSON(j);
    final JSONObject st = IBUtils.readJsonObject(pp);
    JSONAssert.assertEquals(st, j, true);
    final Path od = med.getOutputDirectory();
    for (Path p1 = pp; !p1.equals(od); p1 = p1.getParent()) {

    }

  }
}
