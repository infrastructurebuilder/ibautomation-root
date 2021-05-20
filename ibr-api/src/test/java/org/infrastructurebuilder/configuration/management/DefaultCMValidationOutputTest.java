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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class DefaultCMValidationOutputTest {

  private static final String VALTEXT = "itsalright";
  private IBRValidationOutput d;
  private IBRValidationOutput err;

  @Before
  public void setUp() throws Exception {
    d = new IBRValidationOutput(Paths.get("."), VALTEXT, Optional.empty());
    err = new IBRValidationOutput(Paths.get("./target"), "WHAT THE H*CKS?!?!?",
        Optional.of(new IBArchiveException("This sucks")));
  }

  @Test
  public void testEqualsObject() {
    final IBRValidationOutput e = new IBRValidationOutput(Paths.get("."), "othertext",
        Optional.of(new IBArchiveException("A")));
    assertNotEquals(d, err);
    assertNotEquals(d, null);
    assertNotEquals(d, "X");
    assertEquals(d, e);
    assertEquals(d, d);
  }

  @Test
  public void testError() {
    assertTrue(d.isValid());
    assertFalse(err.isValid());
  }

  @Test
  public void testGetException() {
    assertFalse(d.getException().isPresent());
    assertTrue(err.getException().isPresent());
  }

  @Test
  public void testGetPath() {
    assertEquals(Paths.get("."), d.getPath());
  }

  @Test
  public void testGetValidationText() {
    assertEquals(VALTEXT, d.getValidationText());
  }

  @Test
  public void testHashCode() {
    assertNotEquals(0, d.hashCode());
    assertNotEquals(0, err.hashCode());
  }
}
