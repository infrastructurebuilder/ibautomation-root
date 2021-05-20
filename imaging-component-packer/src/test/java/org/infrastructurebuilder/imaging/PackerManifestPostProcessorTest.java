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
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.infrastructurebuilder.automation.PackerException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerManifestPostProcessorTest extends AbstractPackerTestRoot {

  private PackerManifestPostProcessor mpp;

  @Before
  public void setUp() throws Exception {
    mpp = new PackerManifestPostProcessor();

  }

  @Test
  public void test() {
    final JSONArray j = new JSONArray(Arrays.asList(new JSONObject("{\"type\":\"manifest\",\"strip_path\":false}")));
    final JSONArray q = mpp.asJSONArray();
    JSONAssert.assertEquals(j, q, true);
  }

  @Test
  public void testTypes() {
    assertEquals(0, mpp.getNamedTypes().size());
    assertFalse(mpp.isMultiAuthCapable());
  }

  @Test(expected = PackerException.class)
  public void testValidate() {
    mpp.validate();
  }

  @Test
  public void testValidate2() {
    mpp.setWorkingRootDirectory(getRoot());
    mpp.setTargetDirectory(getTargetDir());
    mpp.validate();
  }

}
