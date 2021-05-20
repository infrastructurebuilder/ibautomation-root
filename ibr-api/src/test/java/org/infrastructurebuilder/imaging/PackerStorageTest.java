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
package org.infrastructurebuilder.imaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PackerStorageTest {

  private ImageStorage s;

  @Before
  public void setUp() throws Exception {
    s = new ImageStorage();
  }

  @Test
  public void testGetDeviceName() {
    assertNull(s.getDeviceName());
    s.setDeviceName("X");
    assertEquals("X", s.getDeviceName());
  }

  @Test
  public void testGetId() {
    assertNull(s.getId());
    s.setId("X");
    assertEquals("X", s.getId());
  }

  @Test
  public void testGetSize() {
    s.setSize(100L);
    assertEquals(100L, s.getSize());
  }

  @Test
  public void testGetVirtualName() {
    assertNull(s.getVirtualName());
    s.setVirtualName("X");
    assertEquals("X", s.getVirtualName());
  }

  @Test
  public void noIndexByDefault() {
    assertEquals(0, s.getIndex());
  }

  @Test
  public void indexed() {
    ImageStorage s2 = s.withIndex(1);
    assertEquals(1, s.getIndex());
    assertEquals(1, s2.getIndex());
    assertTrue(s2 == s);
  }

  @Test
  public void snapshotIdTest() {
    assertNull(s.getSnapshotIdentifier());
    s.setSnapshotIdentifier("abc");
    assertEquals("abc", s.getSnapshotIdentifier());
  }

}
