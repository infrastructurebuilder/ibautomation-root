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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.infrastructurebuilder.imaging.PackerException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerShellProvisionerTest extends PackerShellLocalPostProcessorTest {

  private PackerShellLocalProvisioner p;

  @Before
  public void setUp2() throws Exception {
    p = new PackerShellLocalProvisioner();
  }

  @Test
  public void testGetNamedTypes() {
    assertEquals(2, p.getNamedTypes().size());
  }

  @Test
  public void testSetLocal() {
    p.setLocal(true);
    String x = "{\"type\": \"shell-local\"}";
    JSONObject b = p.asJSON();
    JSONAssert.assertEquals(new JSONObject(x), p.asJSON(), true);
    p.setLocal(false);
    x = "{\"X\": [{\"type\": \"shell\"}]}";
    b = new JSONObject().put("X", p.asJSONArray());
    final JSONObject d = new JSONObject(x);
    JSONAssert.assertEquals(d, b, true);

  }

  @Test(expected = PackerException.class)
  public void testValidate() {
    p.validate();
  }

  @Override
  @Test
  public void testValidate2() {
    p.setInlines(Arrays.asList("#"));
    p.validate();
  }

}
