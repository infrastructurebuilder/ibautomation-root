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
package org.infrastructurebuilder.imaging.aws.ami.builders;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.infrastructurebuilder.imaging.IBRDialectMapper;
import org.infrastructurebuilder.imaging.IBRDialectMapperTest;
import org.infrastructurebuilder.imaging.IBRDialectSupplier;
import org.infrastructurebuilder.imaging.aws.ami.IBRAWSAMISupplier;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSGpu;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSSmall;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSStupid;
import org.junit.Before;
import org.junit.Test;

public class SpecificPackerAWSLinuxDLBuilderTest  extends IBRDialectMapperTest  {

  private SpecificPackerAWSLinuxDLBuilder a;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    ArrayList<IBRDialectSupplier> l = new ArrayList<>(ts);
    l.add(new IBRAWSAMISupplier(ibr, Arrays.asList(new AWSGpu(),new AWSSmall(), new AWSStupid())));
    m = new IBRDialectMapper(ibr, l);
    a = new SpecificPackerAWSLinuxDLBuilder(m);
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(SpecificPackerAWSLinuxDLBuilder.SPECIFIC_AMAZONEBS_LINUX_DL, a.getLookupHint().get());
  }

  @Test
  public void testGetSourceFilterName() {
    assertEquals(PackerAWSBuilder.DEFAULT_AMZN_LINUX_DL_AMI_STRING, a.getSourceFilterName());
  }

}
