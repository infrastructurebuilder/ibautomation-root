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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InternalDependencyTest {

  private IBRInternalDependency dep;
  private Path target;
  private Path test;

  @Before
  public void setUp() throws Exception {
    target = Paths.get(Optional.ofNullable(System.getProperty("target_directory")).orElse("./target")).toAbsolutePath();
    test = target.resolve(UUID.randomUUID().toString());

    dep = new IBRInternalDependency();
    dep.setClassifier(null);
    dep.setType(null);
  }

  @After
  public void teardown() {
    IBUtils.deletePath(test);
  }

  @Test
  public void testFiles() throws IOException {
    assertFalse(dep.getFile().isPresent());
    IBUtils.writeString(test, "ABC");
    dep.setFile(test);
    assertEquals(test, dep.getFile().get());
  }

  @Test
  public void testMatches() {
    final GAV a = new DefaultGAV("ZZ", "YY", "999.999", "jeff");
    final GAV b = new DefaultGAV("G", "A", "C", "999.999", "T");
    final GAV c = new DefaultGAV("G", "A", "999.999", "T");
    new DefaultGAV("G", "A1", "999.999", "T");
    assertFalse(dep.matches(a));
    dep.setGroupId("G");
    assertFalse(dep.validate());
    dep.setArtifactId("A");
    assertFalse(dep.validate());
    dep.setType("T");
    assertTrue(dep.validate());

    assertFalse(dep.matches(a));
    assertFalse(dep.matches(b));
    assertTrue(dep.matches(c));
    dep.setClassifier("C");
    assertFalse(dep.matches(a));
    assertTrue(dep.matches(b));
    assertFalse(dep.matches(c));
  }

  @Test
  public void testMoreMatches() {
    dep.setGroupId("G");
    dep.setArtifactId("A");
    dep.setType("T");
    final GAV c = new DefaultGAV("G", "A", "999.999", "T");
    assertTrue(dep.matches(c));
    dep.setType("X");
    assertFalse(dep.matches(c));
    dep.setType("T");
    assertTrue(dep.matches(c));
    dep.setArtifactId("X");
    assertFalse(dep.matches(c));
  }

  @Test
  public void testRemoting() {

    assertFalse(dep.isSendToRemote());

    dep.setRemote("X");

    assertTrue(dep.isSendToRemote());

    assertEquals("X", dep.getRemote().get());

    dep.setRemote(null);

    assertFalse(dep.isSendToRemote());
  }

  @Test(expected = PackerException.class)
  public void testSetTdir() {
    dep.set_tdir(Paths.get("."));
  }

  @Test
  public void testSetUnpack() {
    assertFalse(dep.isUnpack().isPresent());
    dep.setUnpack(true);
    assertTrue(dep.isUnpack().get());
    assertFalse(dep.isOverwrite());
    dep.setOverwrite(true);
    assertTrue(dep.isOverwrite());
    dep.setType(IBArchive.IBR);
    dep.setUnpack(false);
    assertTrue(dep.isUnpack().get());
  }

  @Test
  public void testTargetDir() {
    assertFalse(dep.getTargetDir().isPresent());
    dep.applyTargetDir(test);
    assertEquals(test, dep.getTargetDir().get());
  }

  @Test
  public void testToString() {
    assertNotNull(dep.toString());
  }
}
