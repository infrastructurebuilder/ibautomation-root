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
package org.infrastructurebuilder.imaging.shell;

import static org.infrastructurebuilder.ibr.IBRConstants.AMAZONEBS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.PackerConstantsV1;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerShellLocalPostProcessorTest {

  private PackerShellPostProcessor p;

  @Before
  public void setUp() throws Exception {
    p = new PackerShellPostProcessor();
  }

  @Test
  public void testAsJSON() {
    String x = "{\"type\": \"shell\"}";
    JSONObject b = p.asJSON();
    JSONAssert.assertEquals(new JSONObject(x), p.asJSON(), true);
    x = "{\"X\": [{\"type\": \"shell\"}]}";
    b = new JSONObject().put("X", p.asJSONArray());
    final JSONObject d = new JSONObject(x);
    JSONAssert.assertEquals(d, b, true);
  }

  @Test
  public void testGetEnvs() {
    assertFalse(p.getEnvs().isPresent());
    Map<String, String> envs = Collections.emptyMap();
    p.setEnvs(envs);
    assertFalse(p.getEnvs().isPresent());
    envs = new HashMap<>();
    envs.put("A", "B");
    p.setEnvs(envs);
    assertTrue(p.getEnvs().isPresent());
    p.setInlines(Arrays.asList("#"));
    p.validate();
  }

  @Test(expected = PackerException.class)
  public void testGetEnvsFail0() {
    assertFalse(p.getEnvs().isPresent());
    final Map<String, String> envs = new HashMap<>();
    envs.put(PackerConstantsV1.PACKER_BUILD_NAME, "X");
    p.setEnvs(envs);
    assertTrue(p.getEnvs().isPresent());
    p.setInlines(Arrays.asList("#"));
    p.validate();
  }

  @Test
  public void testGetExecuteCommand() {
    assertFalse(p.getExecuteCommand().isPresent());
    p.setExecuteCommand("X");
    assertEquals("X", p.getExecuteCommand().get());
  }

  @Test
  public void testGetId() {
    UUID.fromString(p.getId());
  }

  @Test
  public void testGetInlines() {
    assertFalse(p.getInlines().isPresent());
    p.setInlines(Arrays.asList("A"));
    assertEquals("A", p.getInlines().get().iterator().next());
    assertEquals(1, p.getInlines().get().size());
  }

  @Test
  public void testGetInlineShebang() {
    assertFalse(p.getInlineShebang().isPresent());
    p.setInlineShebang("A");
    assertEquals("A", p.getInlineShebang().get());
  }

  @Test
  public void testGetScripts() {
    assertFalse(p.getScripts().isPresent());
    p.setScripts(Arrays.asList(Paths.get(".")));
    assertEquals(Paths.get(".").toAbsolutePath().toString(), p.getScripts().get().iterator().next());
  }

  @Test
  public void testTypes() {
    assertTrue(p.respondsTo(PackerConstantsV1.SHELL));
    assertFalse(p.respondsTo(AMAZONEBS));
  }

  @Test(expected = PackerException.class)
  public void testValidate1() {
    p.validate();
  }

  @Test
  public void testValidate2() {
    p.setInlines(Collections.emptyList());
    p.validate();
  }

  @Test
  public void testValidate3() {
    p.setScripts(Collections.emptyList());
    p.validate();
  }

  @Test(expected = PackerException.class)
  public void testValidate4() {
    p.setInlines(Collections.emptyList());
    p.setScripts(Collections.emptyList());
    p.validate();
  }

}
