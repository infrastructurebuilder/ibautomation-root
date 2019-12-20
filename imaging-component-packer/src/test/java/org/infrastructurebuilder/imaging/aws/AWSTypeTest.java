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
package org.infrastructurebuilder.imaging.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class AWSTypeTest {

  private AWSDeviceType ebs;
  private AWSVirtType hvm;

  @Before
  public void setUp() throws Exception {
    hvm = AWSVirtType.hvm;
    ebs = AWSDeviceType.ebs;
  }

  @Test
  public void test() {
    assertNotNull(AWSConstants.COPY_TO);
    assertEquals(2, AWSVirtType.values().length);
    assertEquals(2, AWSDeviceType.values().length);
    assertEquals(hvm, AWSVirtType.valueOf("hvm"));
    assertEquals(ebs, AWSDeviceType.valueOf("ebs"));
    assertEquals("ebs", ebs.getTypeString());
  }

}