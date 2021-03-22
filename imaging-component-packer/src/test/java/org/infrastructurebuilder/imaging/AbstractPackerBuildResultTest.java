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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER_TYPE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_TIME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_AUTH_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_RUN_UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.jooq.tools.reflect.Reflect;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class AbstractPackerBuildResultTest {

  private AbstractPackerBuildResult a;
  private DefaultIBAuthentication auth1;
  private JSONObject j;
  private Instant now;
  private UUID uuid;

  @Before
  public void setUp() throws Exception {
    auth1 = new DefaultIBAuthentication();
    auth1.setType("fake");
    Reflect.on(auth1).set("id", "A");
    uuid = UUID.randomUUID();
    now = Instant.ofEpochMilli(100L);
    j = new JSONObject()

        .put(BUILDER_TYPE, FakeAbstractPackerBuilder.FAKE)

        .put(BUILD_TIME, now.toEpochMilli())

        .put(NAME, "name")

        .put(PACKER_RUN_UUID, uuid.toString())

        .put(ORIGINAL_AUTH_ID, "A")

    ;

    a = new FakeAbstractPackerBuilder().new FakeBuildResult(j);
  }

  @Test
  public void testGetArtifactInfo() {
    assertEquals("", a.getArtifactInfo().get());
  }

  @Test
  public void testGetBuilderType() {
    assertEquals("fake", a.getBuilderType());
  }

  @Test
  public void testGetBuildTime() {
    assertEquals(now, a.getBuildTime());
  }

  @Test
  public void testGetFiles() {
    assertNotNull(a.getFiles());
  }

  @Test
  public void testGetJSON() {
    final JSONObject expected = new JSONObject(j.toString());
    JSONAssert.assertEquals(expected, a.getJSON(), true);
  }

  @Test
  public void testGetName() {
    assertEquals("name", a.getName());
  }

  @Test
  public void testGetOriginalAuthId() {
    assertEquals("A", a.getOriginalAuthId().get());
  }

  @Test
  public void testGetRunUUID() {
    assertEquals(uuid, a.getRunUUID());
  }

  @Test
  public void testMatchesForAuth() {
    assertTrue(a.matchesForAuth(auth1));
  }

}
