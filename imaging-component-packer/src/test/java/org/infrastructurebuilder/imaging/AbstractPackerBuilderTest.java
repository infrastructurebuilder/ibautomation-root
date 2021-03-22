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
package org.infrastructurebuilder.imaging;

import static org.infrastructurebuilder.ibr.IBRConstants.AMAZONEBS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER_TYPE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_TIME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_RUN_UUID;
import static org.infrastructurebuilder.imaging.file.PackerFileBuilder.FILETYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.aws.ami.builders.PackerAWSBuilder;
import org.infrastructurebuilder.imaging.aws.ami.builders.PackerAWSBuilderDisk;
import org.infrastructurebuilder.imaging.file.PackerFileBuilder;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class AbstractPackerBuilderTest extends AbstractPackerTestRoot {

  private JSONObject    servers;
  AbstractPackerBuilder pb;

  @Before
  public void thisBefore() throws Exception {
    pb = new FakeAbstractPackerBuilder();
    pb.setWorkingRootDirectory(getRoot());
    pb.setTargetDirectory(getTargetDir());
    servers = new JSONObject().put("A", "B");
  }

  @Test(expected = PackerException.class)
  public void testAWSSetFail1() {
    assertFalse(pb.getBlockDeviceMappings().isPresent());
    assertFalse(pb.getDisk().isPresent());
    final PackerAWSBuilderDisk d = new PackerAWSBuilderDisk();
    d.setDeleteOnTermination(true);
  }

  @Test(expected = PackerException.class)
  public void testAWSSetFail2() {
    assertFalse(pb.getBlockDeviceMappings().isPresent());
    assertFalse(pb.getDisk().isPresent());
    final PackerAWSBuilderDisk d = new PackerAWSBuilderDisk();
    d.setEncrypted(true);
  }

  @Test(expected = PackerException.class)
  public void testAWSSetFail3() {
    assertFalse(pb.getBlockDeviceMappings().isPresent());
    assertFalse(pb.getDisk().isPresent());
    final PackerAWSBuilderDisk d = new PackerAWSBuilderDisk();
    d.setNoDevice(false);
  }

  @Test
  public void testForcedTags() {
    assertFalse(pb.getForcedTags().isPresent());
    pb.setForcedTags(Collections.emptyMap());
    assertFalse(pb.getForcedTags().isPresent());
    final Map<String, String> m = new HashMap<>();
    m.put("A", "B");
    pb.setForcedTags(m);
    assertEquals("B", pb.getForcedTags().get().get("A"));
  }

  @Test
  public void testGetAccessGroups() {
    assertFalse(pb.getAccessGroups().isPresent());
    pb.setAccessGroups(Arrays.asList("A", "B"));
    assertFalse(pb.getAccessGroups().isPresent());

  }

  @Test
  public void testGetAvailabilityZone() {
    assertFalse(pb.getAvailabilityZone().isPresent());
    pb.setAvailabilityZone(PackerAWSBuilder.DEFAULT_REGION);
    assertEquals(PackerAWSBuilder.DEFAULT_REGION, pb.getAvailabilityZone().get());
  }

  @Ignore
  @Test
  public void testGetBlockDeviceMappings() {
    assertFalse(pb.getBlockDeviceMappings().isPresent());
    assertFalse(pb.getDisk().isPresent());
    final FakePackerBuilderDisk d = new FakePackerBuilderDisk();
    d.setDeviceName("sdx1");
    d.setSnapshotId("snapshotId");
    d.setVirtualName("ephemeral1");
    d.setVolumeSize(199L);

    final List<ImageDataDisk> disk = Arrays.asList(d);
    pb.setDisk(disk);
    assertEquals(1, pb.getDisk().get().size());
  }

  @Test(expected = PackerException.class)
  public void testGetBlockDeviceMappings2() {
    final PackerFileBuilder fpb = new PackerFileBuilder();
    assertFalse(fpb.getBlockDeviceMappings().isPresent());
    assertFalse(fpb.getDisk().isPresent());
    final FakePackerBuilderDisk d = new FakePackerBuilderDisk();
    d.setDeleteOnTermination(true);
    d.setDeviceName("sdx1");
    d.setEncrypted(true);

    d.setNoDevice(false);
    d.setSnapshotId("snapshotId");
    d.setVirtualName("ephemeral1");
    d.setVolumeSize(199L);

    final List<ImageDataDisk> disk = Arrays.asList(d);
    fpb.setDisk(disk);
  }

  @Test(expected = PackerException.class)
  public void testGetBlockDeviceMappingsInvalidDisk() {
    assertFalse(pb.getBlockDeviceMappings().isPresent());
    assertFalse(pb.getDisk().isPresent());
    final ImageDataDisk d = new InvalidFakeBuilderDisk(Arrays.asList(AMAZONEBS));
    final List<ImageDataDisk> disk = Arrays.asList(d);
    pb.setDisk(disk);
    assertEquals(1, pb.getDisk().get().size());
  }

  @Test
  public void testGetContent() {
    assertFalse(pb.getContent().isPresent());
    pb.setContent("A");

    assertEquals("A", pb.getContent().get());
  }

  @Test
  public void testGetCredentials() {
    assertFalse(pb.getCredentialsProfile().isPresent());
    pb.setCredentialsProfile("A");
    assertEquals("A", pb.getCredentialsProfile().get());
  }

  @Test
  public void testGetDescription() {
    assertFalse(pb.getDescription().isPresent());
    pb.setDescription("A");
    assertEquals("A", pb.getDescription().get());
  }

  @Test
  public void testGetInstanceType() {
    assertFalse(pb.getInstanceType().isPresent());
    pb.setInstanceType("A");
    assertEquals("A", pb.getInstanceType().get());
  }

  @Test
  public void testGetLaunchUser() {
    assertFalse(pb.getLaunchUser().isPresent());
    pb.setLaunchUser("A");
    assertEquals("A", pb.getLaunchUser().get());
  }

  @Test
  public void testGetNetworkId() {
    assertFalse(pb.getNetworkId().isPresent());
    pb.setNetworkId("A");
    assertEquals("A", pb.getNetworkId().get());
  }

  @Test
  public void testGetRegion() {
    assertFalse(pb.getRegion().isPresent());
    pb.setRegion("A");
    assertEquals("A", pb.getRegion().get());
  }

  @Test
  public void testGetRegionalEncFilters() {
    final PackerFileBuilder fpb = new PackerFileBuilder();
    assertFalse(fpb.getCopyToRegions().isPresent());
    final List<String> copyToRegions = new ArrayList<>();
    fpb.setCopyToRegions(copyToRegions);
    assertFalse(fpb.getRegionalEncryptionIdentifiers().isPresent());
    Map<String, String> filters = new HashMap<>();
    fpb.setRegionalEncryptionIdentifiers(filters);
    assertFalse(fpb.getRegionalEncryptionIdentifiers().isPresent());
    filters = new HashMap<>();
    filters.put("C", "D");
    fpb.setRegionalEncryptionIdentifiers(filters);
    assertEquals(1, fpb.getRegionalEncryptionIdentifiers().get().size());
    copyToRegions.add("A");
    fpb.setCopyToRegions(copyToRegions);
    final Map<String, String> g = fpb.getRegionalEncryptionIdentifiers().get();
    assertEquals(2, g.size());
    assertTrue(g.containsKey("C"));
    assertTrue(g.get("A").equals(""));
  }

  @Test
  public void testGetTargetOutput() {
    assertFalse(pb.getTargetOutput().isPresent());
    pb.setTargetOutput(getTargetDir());
    assertEquals(getTargetDir(), pb.getTargetOutput().get());
  }

  @Test
  public void testGetUserData() {
    assertFalse(pb.getUserData().isPresent());
    assertFalse(pb.getUserDataFile().isPresent());
    final Path ud = _getTarget().resolve("test-classes").resolve("fakepacker.sh");
    pb.setUserDataFile(ud);
    assertEquals(ud, pb.getUserDataFile().get());
    assertTrue(pb.getUserData().get().contains("BASH_SOURCE"));
  }

  @Test
  public void testIsValid() {
    pb.validate();
  }

  @Ignore
  @Test
  public void testJSONOutput() {
    pb.setArtifact(new DefaultGAV("org.junit:junit:1.2.3:jar"));
    final JSONObject a = pb.asJSON();
    a.remove(NAME);
    final String x = "{\n" + "  \"shutdown_behavior\": \"stop\",\n"
    + "  \"force_deregister\": true,\n"
        + "  \"type\": \"amazon-ebs\",\n" + "  \"tags\": {},\n"

        + "  \"ami_name\": \"org.junit/junit/1.2.3\",\n"
        + "  \"source_ami_filter\": {\n"
        + "    \"owners\": [\n"
        + "      \"self\",\n"
        + "      \"amazon\"\n" + "    ],\n"
        + "    \"most_recent\": true,\n"
        + "    \"filters\": {\"name\": \"amzn-ami*-ebs\"}\n"
        + "  },\n" + "  \"region\": \"us-west-2\",\n"
        + "  \"force_delete_snapshot\": true\n" + "}";
    JSONAssert.assertEquals(new JSONObject(x), a, true);
  }

  @Test
  public void testMapIt() {
    final JSONObject j = new JSONObject();
    final Instant now = Instant.now();
    j.put(BUILDER_TYPE, FakeAbstractPackerBuilder.FAKE);
    j.put(NAME, FakeAbstractPackerBuilder.FAKE);
    final String uuid = UUID.randomUUID().toString();
    j.put(PACKER_RUN_UUID, uuid);
    j.put(BUILD_TIME, now.toEpochMilli());
    final FakeAbstractPackerBuilder b = new FakeAbstractPackerBuilder();
    b.mapBuildResult(j);
  }

  @Test
  public void testRoot() {
    assertEquals(getRoot(), pb.getWorkingRootDirectory());
  }

  @Test
  public void testSerForceDergister() {
    assertTrue(pb.isForceDeregister());
    pb.setForceDeregister(false);
    assertFalse(pb.isForceDeregister());
  }

  @Test
  public void testSetArtifact() {
    final GAV artifact = new DefaultGAV("org.junit:junit:1.2.3:jar");
    assertFalse(pb.getArtifact().isPresent());
    pb.setArtifact(artifact);
    assertEquals(artifact, pb.getArtifact().get());
  }

  @Test
  public void testSetEncryptionID() {
    assertFalse(pb.getEncryptionIdentifier().isPresent());
    pb.setEncryptionId("A");
    assertEquals("A", pb.getEncryptionIdentifier().get());
  }

  @Test
  public void testSetForceDeleteSS() {
    assertTrue(pb.isForceDeleteSnapshot());
    pb.setForceDeleteSnapshot(false);
    assertFalse(pb.isForceDeleteSnapshot());
  }

  @Test
  public void testSetSourceImage() {
    final PackerFileBuilder b = new PackerFileBuilder();
    assertFalse(b.getSourceFilter().isPresent());
    assertFalse(b.getSourceImage().isPresent());
    b.setSourceImage("A");
    assertEquals("A", b.getSourceImage().get());
    final Map<String, Object> m = new HashMap<>();
    m.put("A", "B");
    b.setSourceFilter(m);
    assertTrue(b.getSourceFilter().isPresent());
    assertFalse(b.getSourceImage().isPresent());
    b.setSourceImage("A");
    assertEquals("A", b.getSourceImage().get());
    assertFalse(b.getSourceFilter().isPresent());
  }

  @Test
  public void testSetSubnetId() {
    assertFalse(pb.getSubnetId().isPresent());
    pb.setSubnetId("A");
    assertEquals("A", pb.getSubnetId().get());
  }

  @Test
  public void testSetTags() {
    assertEquals(0, pb.getTags().size());
    final Map<String, String> m = new HashMap<>();
    m.put("A", "B");
    pb.setTags(m);
    assertEquals(1, pb.getTags().size());
  }

  @Test(expected = PackerException.class)
  public void testTargetDir1() {
    pb.setTargetDirectory(getRoot());
  }

  public void testTargetDir2() {
    final Path t = Paths.get("./x");
    pb.setTargetDirectory(t);
    final Path g = pb.getOutputPath().get();
    pb.setOutputFileName("ABC");
    final Path h = pb.getOutputPath().get();
    assertNotEquals("Changing filename changes path", g, h);
  }

  @Ignore
  @Test
  public void testTypes() {
    assertFalse(pb.respondsTo(FILETYPE));
    assertTrue(pb.respondsTo(AMAZONEBS));
  }

  @Test
  public void testValidate1() {
    final FakeAbstractPackerBuilder b = new FakeAbstractPackerBuilder();
    b.validate();
  }


}
