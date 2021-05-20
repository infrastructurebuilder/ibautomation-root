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
package org.infrastructurebuilder.imaging.file;

import java.util.Collections;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerFileProvisionerTest {

  private PackerFileProvisioner p;

  @Before
  public void setUp() throws Exception {
    p = new PackerFileProvisioner();
    p.setSource("abc");
    p.setDestination("def");
    p.setDirection(PackerFileProvisionerDirection.download);
    p.setGenerated(true);
    p.setBuilders(Collections.emptyList());
  }

  @Test
  public void testAsJSON() {
    final JSONObject k = p.asJSON();
    final JSONObject expected = new JSONObject("{\n" + "  \"destination\": \"def\",\n" + "  \"source\": \"abc\",\n"
        + "  \"type\": \"file\",\n" + "  \"generated\": true,\n" + "  \"direction\": \"download\"\n" + "}");
    JSONAssert.assertEquals(expected, k, true);
  }

  @Test
  public void testValidate() {
    p.validate();
  }

}
