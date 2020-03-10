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
package org.infrastructurebuilder.imaging.container;

import static org.infrastructurebuilder.imaging.container.DockerV1Constants.CONTAINER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.imaging.ImageDataDisk;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.container.PackerContainerBuilder;
import org.infrastructurebuilder.imaging.container.PackerDockerBuilderDisk;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerDockerBuilderTest {

  private DefaultGAV artifact;
  private JSONObject empty;
  private PackerContainerBuilder p;

  @Before
  public void setUp() throws Exception {
    p = new PackerContainerBuilder();
    artifact = new DefaultGAV("X:Y:1.0.0:jar");

    empty = new JSONObject("{\n" + "  \"commit\": true,\n" + "  \"privileged\": false,\n" + "  \"type\": \"docker\",\n"
        + "  \"pull\": true\n" + "}");

  }

  @Ignore
  @Test
  public void testAddRequiredItemsToFactory() {
    fail("Not yet implemented");
  }

  @Test
  public void testAsJSON() {
    JSONAssert.assertEquals(empty, p.asJSON(), true);
    p.setArtifact(artifact);
    JSONAssert.assertEquals(empty, p.asJSON(), true);
  }

  @Test
  public void testGetAuthor() {
    assertFalse(p.getAuthor().isPresent());
    p.setAuthor("X");
    assertEquals("X", p.getAuthor().get());
  }

  @Test
  public void testGetAuthType() {
    assertEquals(CONTAINER, p.getAuthType().get());
  }

  @Test
  public void testGetChanges() {
    assertFalse(p.getChanges().isPresent());
    p.setChanges(Arrays.asList("CMD Y"));
    assertTrue(p.getChanges().isPresent());
    assertEquals("CMD Y", p.getChanges().get().iterator().next());
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(CONTAINER, p.getLookupHint().get());
  }

  @Test
  public void testGetNamedTypes() {
    assertEquals(1, p.getNamedTypes().size());
    assertEquals(CONTAINER, p.getNamedTypes().iterator().next());
  }

  @Test
  public void testGetPackerType() {
    assertEquals(CONTAINER, p.getPackerType());
  }

  @Test
  public void testGetRunCommands() {
    assertFalse(p.getRunCommands().isPresent());
    p.setRunCommands(Arrays.asList("X"));
    assertEquals(1, p.getRunCommands().get().size());
    assertEquals("X", p.getRunCommands().get().iterator().next());
  }

  @Test
  public void testGetSizes() {
    assertEquals(1, p.getSizes().size());
  }

  @Test
  public void testGetVolumes() {
    assertFalse(p.getVolumes().isPresent());
    final PackerDockerBuilderDisk disk = new PackerDockerBuilderDisk();
    disk.setHostPath("/");
    disk.setContainerPath("/");
    final List<ImageDataDisk> d = Arrays.asList(disk);
    p.setDisk(d);
    assertEquals(1, p.getVolumes().get().size());
  }

  @Test
  public void testIsPrivileged() {
    assertFalse(p.isPrivileged());
    p.setPrivileged(true);
    assertTrue(p.isPrivileged());
  }

  @Ignore
  @Test
  public void testMapBuildResult() {
    fail("Not yet implemented");
  }

  @Test
  public void testValidate() {
    try {
      p.validate();
    } catch (final PackerException e) {
      fail("Need more testing");
    }
  }

}
