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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE_CLASS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TARGET;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerHintMapDAOTest {

  private PackerHintMapDAO d;
  private JSONObject j;

  @Before
  public void setUp() throws Exception {
    j = new JSONObject().put(ID, UUID.randomUUID().toString())

        .put(TYPE, TYPE)

        .put(TARGET, TARGET)

        .put(SOURCE, SOURCE)

        .put(SOURCE_CLASS, String.class.getCanonicalName());
    d = new PackerHintMapDAO(j);
  }

  @Test
  public void testAsJSON() {
    JSONAssert.assertEquals(j, d.asJSON(), true);
  }

  @Test
  public void testGetHint() {
    assertEquals(SOURCE, d.getHint().get());
  }

  @Test
  public void testGetKlass() {
    assertEquals(String.class, d.getKlass());
  }

  @Test
  public void testGetTarget() {
    assertEquals(TARGET, d.getTarget().get());
  }

  @Test
  public void testGetType() {
    assertEquals(TYPE, d.getType().get());
  }

  @Test
  public void testOtherCons() {
    final IBRHintMap hm = new PackerHintMapDAO(Optional.of(new FakePackerAuthentication(Paths.get("target"))),
        new FakePackerBaseObject(null));
    assertNotNull(hm);
  }
}
