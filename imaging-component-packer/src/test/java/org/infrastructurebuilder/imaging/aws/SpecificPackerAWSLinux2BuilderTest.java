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

import org.junit.Before;
import org.junit.Test;

public class SpecificPackerAWSLinux2BuilderTest {

  private SpecificPackerAWSLinux2Builder a;

  @Before
  public void setUp() throws Exception {
    a = new SpecificPackerAWSLinux2Builder(new FakeIBRAWSMapper());
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(SpecificPackerAWSLinux2Builder.SPECIFIC_AMAZONEBS_LINUX2, a.getLookupHint().get());
  }

  @Test
  public void testGetSourceFilterName() {
    assertEquals(PackerAWSBuilder.DEFAULT_AMZN2_LINUX_AMI_STRING, a.getSourceFilterName());
  }

}
