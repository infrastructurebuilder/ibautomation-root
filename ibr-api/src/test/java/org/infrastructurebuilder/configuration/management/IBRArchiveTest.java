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
package org.infrastructurebuilder.configuration.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.core.IBUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IBRArchiveTest {

  private Path      target;
  private Path      test;
  private IBArchive archive;
  private Path      test2;
  private Path      test4;

  @Before
  public void setUp() throws Exception {
    target = Paths.get(Optional.ofNullable(System.getProperty("target_directory")).orElse("./target")).toAbsolutePath();
    test = target.resolve(UUID.randomUUID().toString());
    test2 = target.resolve(UUID.randomUUID().toString());
    test4 = test.relativize(test2);
    archive = new IBArchive(test);
  }

  @After
  public void tearDown() throws Exception {
    try {
      IBUtils.deletePath(test);
      IBUtils.deletePath(test2);
    } catch (final Throwable t) {

    }
  }

  @Test
  public void testAddMetadata() {

    archive.addMetadata("X", test2);
    final JSONObject actual = archive.asJSON();
    actual.remove("id");
    final JSONObject expected = new JSONObject();
    test.relativize(test2).toString();
    expected.put("data", new JSONArray(Arrays.asList(new JSONObject().put("X", test2))));
    final IBArchive archive2 = new IBArchive(actual.put("id", UUID.randomUUID().toString()), test);

    try {
      archive2.addMetadata("X", Paths.get("."));
      fail("This should fail");
    } catch (final IBArchiveException e) {

    }
  }

  @Test
  public void testAddMetadataAddl() {
    final Path tt = test2.toAbsolutePath();
    archive.addMetadata("X", tt);
    final Path t3 = archive.getPathList().iterator().next().getPath();
    assertNotEquals(t3, tt);
    assertEquals(t3, test4);
  }

  @Test
  public void testAddMetadataAddl2() {
    archive.addMetadata("X", test4);
    final Path t3 = archive.getPathList().iterator().next().getPath();
    assertEquals(t3, test4);
  }

  @Test
  public void testGetPathList() {
    archive.addMetadata("X", test2);
    assertTrue(archive.getPathList().size() == 1);

    List<String> x1 = archive.getPathKeys();
    assertEquals(1, x1.size());
  }

}
