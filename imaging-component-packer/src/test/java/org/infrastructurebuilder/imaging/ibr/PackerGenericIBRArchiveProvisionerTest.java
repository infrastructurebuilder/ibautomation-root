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
package org.infrastructurebuilder.imaging.ibr;

import static org.infrastructurebuilder.util.IBUtils.readToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.infrastructurebuilder.configuration.management.FakeTestCMType;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.infrastructurebuilder.imaging.maven.PackerBeanTest;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackerGenericIBRArchiveProvisionerTest {
  public final static Logger log = LoggerFactory.getLogger(PackerGenericIBRArchiveProvisionerTest.class);
  private static final String TESTING = "testing";
  private IBArchive a;
  private DefaultPlexusContainer c;
  private PackerGenericIBRArchiveProvisioner d;
  private List<PackerProvisioner<JSONObject>> dList;
  private ContainerConfiguration dpcreq;
  private AbstractPackerIBRProvisioner<JSONObject> g;
  private List<PackerProvisioner<JSONObject>> gList;
  private ClassWorld kw;
  private IBArchive l;
  private Path root;
  private Path target;
  private final WorkingPathSupplier wps = new WorkingPathSupplier();

  @Before
  public void setUp() throws Exception {
    target = wps.getRoot();
    root = wps.get();

    final String mavenCoreRealmId = TESTING;
    kw = new ClassWorld(mavenCoreRealmId, PackerBeanTest.class.getClassLoader());

    dpcreq = new DefaultContainerConfiguration().setClassWorld(kw).setClassPathScanning(PlexusConstants.SCANNING_INDEX)
        .setName(TESTING);
    c = new DefaultPlexusContainer(dpcreq,
        new WireModule(new SpaceModule(new URLClassSpace(kw.getClassRealm(TESTING)))));

    g = (AbstractPackerIBRProvisioner<JSONObject>) c.lookup(PackerProvisioner.class,
        PackerGenericIBRArchiveProvisioner.GENERIC_IBR);
    g.setBuilders(new ArrayList<>());
    g.setLog(log);
    final JSONObject j = new JSONObject(readToString(getClass().getResourceAsStream("/testAutomationArchive1.json")));
    final JSONArray ja = j.getJSONArray("data");
    ja.getJSONObject(0).put(FakeTestCMType.FAKE_TEST_CMTYPE, UUID.randomUUID().toString());
    a = new IBArchive(j, root);
    final JSONObject k = new JSONObject(j.toString());
    k.getJSONArray("data").put(new JSONObject().put("X", "Y"));
    l = new IBArchive(k, root);
    assertNotNull(l);
    g.setWorkingRootDirectory(root);
    gList = g.applyArchive(a);
    assertTrue(gList.size() > 0);
    d = (PackerGenericIBRArchiveProvisioner) c.lookup(PackerProvisioner.class,
        PackerGenericIBRArchiveProvisioner.GENERIC_IBR);
    d.setBuilders(new ArrayList<>());
    d.setLog(log);
    d.setWorkingRootDirectory(root);
    dList = d.applyArchive(a);
    assertTrue(dList.size() > 0);
    assertFalse(d.getNamedTypes().isEmpty());
  }

  @Test
  public void testAsJSON() {
    JSONAssert.assertEquals(new JSONObject(), d.asJSON(), true);
  }

  @Test
  public void testConstructor() {
    assertNotNull(g);
  }

  @Test
  public void testGetArchive() {
    assertEquals(a, d.getArchive().get());
  }

  @Test
  public void testGetLocalType() {
    assertEquals(PackerGenericIBRArchiveProvisioner.GENERIC_IBR, d.getLookupHint().get());
  }

  @Test
  public void testGetNamedTypes() {
    assertTrue(d.getNamedTypes().contains(FakeTestCMType.FAKE_TEST_CMTYPE));
  }

  @Test
  public void testGetRoot() {
    assertEquals(root, d.getWorkingRootDirectory());
  }

  @Test
  public void testValidate() {
    d.validate();
  }

  @Test(expected = PackerException.class)
  public void testValidate2() {
    d = new PackerGenericIBRArchiveProvisioner(Collections.emptyMap());
    d.setBuilders(Collections.emptyList());
    d.validate();
  }

}
