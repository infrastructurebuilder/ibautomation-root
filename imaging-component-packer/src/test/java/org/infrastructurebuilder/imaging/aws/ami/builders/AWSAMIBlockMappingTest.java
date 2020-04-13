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
package org.infrastructurebuilder.imaging.aws.ami.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.ibr.IBRConstants;
import org.infrastructurebuilder.imaging.ImageStorage;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class AWSAMIBlockMappingTest {

  private static final String NAME = "name";
  private PackerAWSBuilderDisk bm;
  private ImageStorage s;

  @Before
  public void setUp() throws Exception {
    bm = new PackerAWSBuilderDisk();
    s = new ImageStorage();
    s.setDeviceName("X");
    s.setId("Y");
    s.setSize(100);
    s.setVirtualName("Z");
  }

  @Test
  public void testAsJSON() {
    final JSONObject jj2 = new JSONObject("{\n" + "  \"no_device\": false,\n" + "  \"device_name\": \"/dev/xvda\",\n"
        + "  \"delete_on_termination\": true,\n" + "  \"volume_type\": \"gp2\",\n" + "  \"encrypted\": false\n" + "}");
    final JSONObject jj = bm.asJSON();
    JSONAssert.assertEquals(jj2, jj, true);
  }

  @Test
  public void testCons() {
    PackerAWSBuilderDisk gg;
    gg = new PackerAWSBuilderDisk(s);
    assertNotNull(gg);
  }

  @Test
  public void testGetDeviceName() {
    assertEquals("/dev/xvda", bm.getDeviceName().get());
    bm.setDeviceName(NAME);
    assertEquals(NAME, bm.getDeviceName().get());

    assertEquals(IBRConstants.AMAZONEBS, bm.getNamedTypes().iterator().next());
  }

  @Test
  public void testGetKMSKeyId() {
    assertFalse(bm.getEncryptionIdentifier().isPresent());
    bm.setEncryptionIdentifier(new AWSPackerEncryptionIdentifier(NAME));
    assertEquals(NAME, bm.getEncryptionIdentifier().get().getEncryptionIdentifierAsString());
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(IBRConstants.AMAZONEBS, bm.getLookupHint().get());
  }

  @Test
  public void testGetSnapshotId() {
    assertFalse(bm.getSnapshotId().isPresent());
    bm.setSnapshotId(NAME);
    assertEquals(NAME, bm.getSnapshotId().get());
  }

  @Test
  public void testGetVirtualNamed() {
    assertFalse(bm.getVirtualName().isPresent());
    bm.setVirtualName(NAME);
    assertEquals(NAME, bm.getVirtualName().get());
  }

  @Test
  public void testGetVolumeSize() {
    assertFalse(bm.getVolumeSize().isPresent());
    bm.setVolumeSize(10L);
    assertEquals(new Long(10L), bm.getVolumeSize().get());
  }

  @Test
  public void testIsValid() {
    assertTrue(bm.isValid());
  }

  @Test(expected = PackerException.class)
  public void testSetVolumeType() {
    assertEquals(AWSBlockMappingType.gp2.name(), bm.getVolumeType().get());
    bm.setVolumeType(AWSBlockMappingType.io1.name());
  }

  @Test
  public void testValidate() {
    bm.validate();
  }

}
