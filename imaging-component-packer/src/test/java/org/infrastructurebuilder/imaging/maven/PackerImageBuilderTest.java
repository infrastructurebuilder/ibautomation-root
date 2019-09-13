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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infrastructurebuilder.imaging.ImageStorage;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerSizing;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.junit.Before;
import org.junit.Test;

public class PackerImageBuilderTest {

  private PackerImageBuilder p;
  private ImageStorage s;

  @Before
  public void setUp() throws Exception {
    p = new PackerImageBuilder();
    s = new ImageStorage();

  }

  @Test
  public void testAddDisk() {
    assertEquals(0, p.getDisks().size());
    p.addDisk(s);
    assertEquals(1, p.getDisks().size());
    p.setDisks(Collections.emptyList());
    assertEquals(1, p.getDisks().size());
  }

  @Test(expected = PackerException.class)
  public void testAddDupePostProcessingHint() {
    assertEquals(0, p.getPostProcessingHints().size());
    p.addPostProcessingHint("hint");
    assertEquals(1, p.getPostProcessingHints().size());
    p.addPostProcessingHint("hint");
  }

  @Test(expected = PackerException.class)
  public void testAddDupeType() {
    final List<Type> j1 = new ArrayList<>();
    final Type x = new Type();
    x.setHint("X");
    j1.add(x);
    final Type a = new Type();
    a.setParent(new DefaultGAV("X", "A", null, "1.0.0", "packer"));
    a.setHint("Y");
    j1.add(a);
    p.setTypes(j1);
    p.setTypes(j1);
  }

  @Test
  public void testAddPostProcessingHint() {
    assertEquals(0, p.getPostProcessingHints().size());
    p.addPostProcessingHint("hint");
    assertEquals(1, p.getPostProcessingHints().size());
    assertEquals("hint", p.getPostProcessingHints().iterator().next());
    p.setPostProcessors(Collections.emptyList());
    assertEquals(1, p.getPostProcessingHints().size());
  }

  @Test
  public void testGetSize() {
    assertEquals(PackerSizing.small, p.getSize());
    p.setSize(PackerSizing.stupid);
    assertEquals(PackerSizing.stupid, p.getSize());
  }

  @Test
  public void testGetTags() {
    Map<String, String> t = p.getTags();
    assertNotNull(t);
    assertEquals(0, t.size());
    t = new HashMap<>();
    t.put("X", "Y");
    p.setTags(t);
    assertEquals(1, t.size());
  }

  @Test
  public void testGetTypes() {
    final List<Type> j = p.getTypes();
    assertEquals(0, j.size());
    final List<Type> j1 = new ArrayList<>();
    final Type x = new Type();
    x.setHint("X");
    j1.add(x);
    final Type a = new Type();
    a.setParent(new DefaultGAV("X", "A", null, "1.0.0", "packer"));
    a.setHint("Y");
    j1.add(a);
    p.setTypes(j1);
    assertEquals(2, p.getTypes().size());
    assertFalse(p.getTypes().stream().filter(w -> w.getHint().equals("Z")).findFirst().isPresent());

  }

}
