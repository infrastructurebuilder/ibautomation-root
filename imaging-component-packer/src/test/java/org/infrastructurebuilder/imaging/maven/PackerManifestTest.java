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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.infrastructurebuilder.imaging.ImageBuilder;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerHintMapDAO;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducerFactory;
import org.joor.Reflect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackerManifestTest extends PackerExecutionDataTest {
  static final Logger log = LoggerFactory.getLogger(PackerManifestTest.class);

  protected List<PackerManifest> artifactData;
  protected IBAuthenticationProducerFactory authFactory;
  protected Path authTestPath;
  protected DefaultPlexusContainer c;
  protected GAV coords;

  protected PackerFactory<JSONObject> factory;

  protected JSONObject j;

  protected Path packer;

  protected ImageBuilder packerImageBuilder;

  protected PackerManifest pm;

  protected Path targetPath;

  protected Path temp;

  @Before
  public void setUp2() throws Exception {
    final String target = Optional.ofNullable(System.getProperty("target")).orElse("./target");
    targetPath = Paths.get(target).toRealPath().toAbsolutePath();
    temp = targetPath.resolve(UUID.randomUUID().toString());
    Files.createDirectories(temp);
    authTestPath = targetPath.resolve("test-auth");
    authFactory = new DummyNOPAuthenticationProducerFactory(() -> authTestPath);
    coords = new DefaultGAV("x", "y", "1.0.0", "jar");
    j = new JSONObject();
    c = new DefaultPlexusContainer();
    packerImageBuilder = new PackerImageBuilder();
    artifactData = Collections.emptyList();
    factory = new DefaultPackerFactory(c, log, targetPath, temp, Paths.get(UUID.randomUUID().toString()), artifactData,
        packerImageBuilder, authFactory, targetPath.resolve("packer"), new Properties(), coords,
        Collections.emptyList(), true);
    final Map<String, Map<String, PackerHintMapDAO>> hintMap = Collections.emptyMap();

    Reflect.on(factory).set("hintMap", hintMap);
    j.put(BUILDS, new JSONArray());
    pm = new PackerManifest(pe, coords, j, "name", "desc", c, factory);
  }

  @Test
  public void testAsJSON() {
    final JSONObject jj = pm.asJSON();
    assertNotNull(jj);
    assertNotNull(new PackerManifest(jj, c));
  }

  @Test
  public void testGetBuilds() {
    assertNotNull(pm.getBuilds());
  }

  @Test
  public void testGetBuildsForType() {
    assertNotNull(pm.getBuildsForType(new DefaultIBAuthentication()));
  }

  @Test
  public void testGetBuildWithId() {
    assertNotNull(pm.getBuildWithId(UUID.randomUUID()));
  }

  @Test
  public void testGetCoords() {
    assertEquals(coords, pm.getCoords().get());
  }

  @Test
  public void testGetLastRun() {
    assertNotNull(pm.getLastRun());
  }

  @Test
  public void testGetMergedManifest() {
    assertNotNull(pm.getMergedManifest());
  }

}
