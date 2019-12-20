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
package org.infrastructurebuilder.configuration.management;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class AbstractIBRTypeTest {

  AbstractIBRType<JSONObject> t;
  final WorkingPathSupplier wps = new WorkingPathSupplier();
  final Path target = wps.getRoot();

  @Test
  public void testGetRoot() {
    Path p = wps.get();
    IBUtils.deletePath(p);
    DummyIBRType.getRps().setPath(p);
    t = new DummyIBRType();
    assertFalse(Files.exists(p));
    Path r = t.getRoot();
    assertEquals(p,r);
    assertTrue(Files.exists(r));

  }


}