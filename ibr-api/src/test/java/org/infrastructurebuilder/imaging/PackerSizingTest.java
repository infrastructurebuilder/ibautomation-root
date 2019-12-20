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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.infrastructurebuilder.imaging.PackerSizing;
import org.junit.Before;
import org.junit.Test;

public class PackerSizingTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void test() {
    final PackerSizing a = PackerSizing.small;
    assertTrue(a.compareTo(PackerSizing.stupid) < 0);
    assertEquals(6, PackerSizing.values().length);
    assertEquals(a, PackerSizing.valueOf("small"));
  }

}