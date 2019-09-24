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
package org.infrastructurebuilder.maven.ibr;

import static org.infrastructurebuilder.configuration.management.IBRConstants.ID;
import static org.infrastructurebuilder.configuration.management.IBRConstants.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class IBRBuilderConfigEntryImplTest {

  private DefaultIBRBuilderConfigElement e;

  @Before
  public void setUp() throws Exception {
    e = new DefaultIBRBuilderConfigElement();
  }

  @Test
  public void testAsJSON() {
    final String x = "{\n" + "  \"test\": false,\n" + "  \"config\": {},\n" + "}";
    final JSONObject j = new JSONObject(x);
    final JSONObject actual = e.asJSON();
    j.put(ID, actual.getString(ID));
    JSONAssert.assertEquals(j, actual, true);
  }

  @Test
  public void testGetId() {
    assertNotNull(e.getId());
  }

  @Test
  public void testGetType() {
    assertNull(e.getType());
    e.setType(TYPE);
    assertEquals(TYPE, e.getType());
  }

  @Test
  public void testGetTypeConfig() {
    assertNotNull(e.get());
    e.setConfig(Collections.emptyMap());
    assertEquals(Collections.emptyMap(), e.get());
  }

  @Test
  public void testIsTest() {
    assertFalse(e.isTest());
    e.setTest(true);
    assertTrue(e.isTest());
  }

}
