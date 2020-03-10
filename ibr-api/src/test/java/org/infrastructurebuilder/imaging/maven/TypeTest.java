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
package org.infrastructurebuilder.imaging.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.junit.Before;
import org.junit.Test;

public class TypeTest {

  private Type t;
  private DefaultGAV p;

  @Before
  public void setUp() throws Exception {
    t = new Type();
    p = new DefaultGAV("A", "B", "C", "1.0", "test");
  }

  @Test(expected = PackerException.class)
  public void testGetBadHint() {
    t.getHint();
  }

  @Test
  public void testSetExtra() {
    assertFalse(t.getExtra().isPresent());
    final Map<String, String> j = new HashMap<>();
    t.setExtra(j);
    assertEquals(j, t.getExtra().get());
  }

  @Test
  public void testSetHint() {
    t.setHint("X");
    assertEquals("X", t.getHint());
  }

  @Test
  public void testSetParent() {
    assertFalse(t.getParent().isPresent());
    t.setParent(p);
    assertEquals(p, t.getParent().get());
  }

}
