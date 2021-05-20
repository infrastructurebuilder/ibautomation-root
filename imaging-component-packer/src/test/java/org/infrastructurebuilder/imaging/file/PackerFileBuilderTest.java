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
package org.infrastructurebuilder.imaging.file;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER_TYPE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_TIME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_RUN_UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.UUID;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.file.PackerFileBuilder.PackerFileBuildResult;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class PackerFileBuilderTest {

  private PackerFileBuilder p;

  @Before
  public void setUp() throws Exception {
    p = new PackerFileBuilder();
  }

  @Test
  public void testAsJSON() {
    assertNotNull(p.asJSON());
  }

  @Test
  public void testGetAuthType() {
    assertFalse(p.getAuthType().isPresent());
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(PackerFileBuilder.FILETYPE, p.getLookupHint().get());
  }

  @Test
  public void testGetNamedTypes() {
    assertEquals(1, p.getNamedTypes().size());
  }

  @Test
  public void testGetPackerType() {
    assertEquals(PackerFileBuilder.FILETYPE, p.getPackerType());
  }

  @Test
  public void testGetSizes() {
    assertEquals(1, p.getSizes().size());
  }

  @Test
  public void testMapBuildResult() {
    final JSONObject l = new JSONObject();
    l.put(BUILDER_TYPE, PackerFileBuilder.FILETYPE);
    l.put(BUILD_TIME, Instant.now().toEpochMilli());
    l.put(NAME, "name");
    l.put(PACKER_RUN_UUID, UUID.randomUUID().toString());
    final PackerFileBuildResult g = (PackerFileBuildResult) p.mapBuildResult(l);
    assertNotNull(g);
  }

  @Test(expected = PackerException.class)
  public void testValidate() {
    p.validate();
  }

}
