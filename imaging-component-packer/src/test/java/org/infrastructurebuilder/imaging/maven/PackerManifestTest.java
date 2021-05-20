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
package org.infrastructurebuilder.imaging.maven;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.infrastructurebuilder.imaging.ImageBuilder;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerHintMapDAO;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducerFactory;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.executor.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;
import org.joor.Reflect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;


public class PackerManifestTest extends PackerExecutionDataTest {
  static final Logger                        log  = System.getLogger(PackerManifestTest.class.getName());
  static final TestingPathSupplier           wps  = new TestingPathSupplier();
  protected VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());

  protected List<PackerManifest>            artifactData;
  protected IBAuthenticationProducerFactory authFactory;
  protected Path                            authTestPath;
  protected DefaultPlexusContainer          c;
  protected GAV                             coords;

  protected PackerFactory factory;

  protected JSONObject j;

  protected Path packer;

  protected ImageBuilder packerImageBuilder;

  protected PackerManifest pm;

  protected Path targetPath;

  protected Path temp;

  @Before
  public void setUp2() throws Exception {
    targetPath = wps.getRoot();
    temp = wps.get();
    Files.createDirectories(temp);
    authTestPath = targetPath.resolve("test-auth");
    authFactory = new DummyNOPAuthenticationProducerFactory(() -> authTestPath);
    coords = new DefaultGAV("x", "y", "1.0.0", "jar");
    j = new JSONObject();
    c = new DefaultPlexusContainer();
    packerImageBuilder = new PackerImageBuilder();
    artifactData = emptyList();
    factory = new DefaultPackerFactory(vpef, c, log, targetPath, temp, artifactData, packerImageBuilder, authFactory,
        targetPath.resolve("packer"), new Properties(), coords, emptyList(), true);
    final Map<String, Map<String, PackerHintMapDAO>> hintMap = emptyMap();

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
