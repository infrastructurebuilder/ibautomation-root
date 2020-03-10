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
package org.infrastructurebuilder.imaging.aws;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.imaging.PackerSizing2;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class AWSPackerBuilderTest {

  private PackerAWSBuilder pb;

  @Before
  public void setUp() throws Exception {
    pb = new PackerAWSBuilder(new FakeIBRAWSMapper());
    pb.setSourceImage("A");
    final GAV artifact = new DefaultGAV("org.junit:junit:1.2.3:jar");
    pb.setArtifact(artifact);

  }

  @Test
  public void testAsJSON() {
    final JSONObject j = pb.asJSON();
    j.remove(NAME);
    final JSONObject expected = new JSONObject("{\n" + "  \"shutdown_behavior\": \"stop\",\n"
        + "  \"force_deregister\": true,\n" + "  \"type\": \"amazon-ebs\",\n" + "  \"tags\": {},\n"

        + "  \"ami_name\": \"org.junit/junit/1.2.3\",\n" + "  \"region\": \"us-west-2\",\n"
        + "  \"source_ami\": \"A\",\n" + "  \"force_delete_snapshot\": true\n" + "}");
    JSONAssert.assertEquals(expected, j, true);
  }

  @Test
  public void testGetDeviceType() {
    assertNotNull(pb.getDeviceType());
    assertFalse(pb.getDeviceType().isPresent());
    pb.setDeviceType(AWSDeviceType.ebs.name());
    assertTrue(pb.getDeviceType().isPresent());
  }

  @Test
  public void testGetNamedTypes() {
    final List<String> x = pb.getNamedTypes();
    assertNotNull(x);
    assertEquals(1, x.size());
  }

  @Test
  public void testGetSourceFilter() {
    final Optional<Map<String, Object>> sf = pb.getSourceFilter();
    assertTrue(sf.isPresent());
    final Map<String, Object> sf2 = new HashMap<>();
    pb.setSourceFilter(sf2);
    assertFalse(sf.get().size() == sf2.size());
  }

  @Test
  public void testGetSourceFilterName() {
    final String j = pb.getSourceFilterName();
    assertNotNull(j);
  }

  @Test
  public void testGetSpotPrice() {
    assertFalse(pb.getSpotPrice().isPresent());
    pb.setSpotPrice("0");
    assertFalse(pb.getSpotPrice().isPresent());
    pb.setSpotPrice("1");
    assertEquals("1", pb.getSpotPrice().get());
    assertFalse(pb.getSpotPriceAutoProduct().isPresent());
    pb.setSpotPrice("auto");
    assertEquals("auto", pb.getSpotPrice().get());
    assertTrue(pb.getSpotPriceAutoProduct().isPresent());
  }

  @Test
  public void testGetSpotPriceAutoProduct() {
    final Optional<String> v = pb.getSpotPriceAutoProduct();
    assertNotNull(v);
    assertFalse(v.isPresent());
    pb.setSpotPrice("100");
    assertTrue(pb.getSpotPrice().isPresent());
    pb.setSpotPrice("auto");
    assertTrue(pb.getSpotPriceAutoProduct().isPresent());
  }

  @Test
  public void testGetVirtType() {
    final Optional<AWSVirtType> g = pb.getVirtType();
    assertFalse(g.isPresent());
    pb.setVirtType(AWSVirtType.paravirtual.name());
    assertTrue(pb.getVirtType().isPresent());
  }

  @Test
  public void testIDEqualName() {
    assertEquals(pb.getId(), pb.getBuildExecutionName());
  }

  @Test
  public void testSetBiuildName() {
    pb.setBuildExecutionName("X");
    assertEquals("X", pb.getBuildExecutionName());
  }

  @Test
  public void testSetters() {
    assertFalse(pb.getDeviceType().isPresent());
    pb.setDeviceType(AWSDeviceType.ebs.name());
    assertTrue(pb.getDeviceType().isPresent());
    assertFalse(pb.getVirtType().isPresent());
    pb.setVirtType(AWSVirtType.hvm.name());
    assertTrue(pb.getVirtType().isPresent());
  }

  @Test
  public void testSizing() {
    assertEquals(PackerSizing2.values().length, pb.getSizes().size());
  }

  @Test
  public void testValidate() {
    pb.validate();
  }
}
