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
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.infrastructurebuilder.automation.PackerException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerChecksumPostProcessorTest extends AbstractPackerTestRoot {

  private PackerChecksumPostProcessor p;

  @Before
  public void setUp() throws Exception {
    p = new PackerChecksumPostProcessor();
  }

  @Test
  public void testAsJSON() {
    final JSONObject j = p.asJSON();
    final JSONObject expected = new JSONObject("{\"type\":\"checksum\",\"checksum_types\":[\"sha512\"]}");
    JSONAssert.assertEquals(expected, j, true);

  }

  @Test
  public void testGetFileName() {
    final UUID uuid = UUID.fromString(p.getFileName());
    assertNotNull(uuid);
  }

  @Test
  public void testGetNamedTypes() {
    assertEquals(0, p.getNamedTypes().size());
  }

  @Test(expected = PackerException.class)
  public void testValidate1() {
    p.validate();
  }

  @Test
  public void testValidate2() {
    p.setTargetDirectory(getTargetDir());
    p.validate();
  }

}
