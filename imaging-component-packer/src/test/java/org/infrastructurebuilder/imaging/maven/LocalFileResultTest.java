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
package org.infrastructurebuilder.imaging.maven;

import static org.infrastructurebuilder.imaging.maven.LocalFileResult.NAME;
import static org.infrastructurebuilder.imaging.maven.LocalFileResult.SIZE;
import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class LocalFileResultTest {

  private JSONObject valid;

  @Before
  public void setUp() throws Exception {
    valid = new JSONObject().put(NAME, NAME).put(SIZE, 1L);
  }

  @Test
  public void testLocalFileResult() {
    final LocalFileResult b = new LocalFileResult(valid);
    assertEquals(b.getName(), NAME);
    assertEquals(b.getSize(), 1L);
  }

}
