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
import org.infrastructurebuilder.imaging.IBRDialectSupplier;
import org.infrastructurebuilder.imaging.aws.ami.IBRAWSAMISupplier;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSGpu;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSSmall;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSStupid;
import org.infrastructurebuilder.imaging.maven.PackerManifestTest;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.junit.Before;
import org.junit.Test;

public class TestPackerBaseAWSLinuxSpecificBuilder extends PackerManifestTest {

  private DefaultIBAuthentication a;

  private SpecificPackerBaseAWSLinuxDLBuilder b;

  @Before
  public void setUp3() throws Exception {
    ArrayList<IBRDialectSupplier> l = new ArrayList<>(ts);
    l.add(new IBRAWSAMISupplier(ibr, Arrays.asList(new AWSGpu(),new AWSSmall(), new AWSStupid())));
    m = new IBRDialectMapper(ibr, l);
    b = new SpecificPackerBaseAWSLinuxDLBuilder(m);
    a = new DefaultIBAuthentication();
  }

  @Test
  public void testAddRequiredItemsToFactory() {
    b.addRequiredItemsToFactory(a, factory);
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(SpecificPackerBaseAWSLinuxDLBuilder.SPECIFIC_AMAZONEBS_BASE_LINUX_DL, b.getLookupHint().get());
  }

}
