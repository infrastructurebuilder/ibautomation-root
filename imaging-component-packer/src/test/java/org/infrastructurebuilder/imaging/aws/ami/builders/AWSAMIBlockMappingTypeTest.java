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
package org.infrastructurebuilder.imaging.aws.ami.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class AWSAMIBlockMappingTypeTest {

  private AWSBlockMappingType t;

  @Before
  public void setUp() throws Exception {
    t = AWSBlockMappingType.valueOf("io1");
  }

  @Test
  public void test() {
    assertTrue(Arrays.asList(AWSBlockMappingType.values()).contains(t));
    assertEquals(AWSBlockMappingType.io1, t);
  }

}
