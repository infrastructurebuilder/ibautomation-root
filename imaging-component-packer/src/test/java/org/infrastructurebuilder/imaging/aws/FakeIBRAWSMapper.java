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

import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.imaging.IBRDialectMapper;
import org.infrastructurebuilder.imaging.IBRDialectSupplier;
import org.infrastructurebuilder.imaging.IBRInstanceType;
import org.infrastructurebuilder.imaging.aws.ami.IBRAWSAMISupplier;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSGpu;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSLarge;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSMedium;
import org.infrastructurebuilder.imaging.aws.ami.sizes.AWSSmall;

public class FakeIBRAWSMapper extends IBRDialectMapper {
  public final static AutomationUtils       ibr     = new AutomationUtilsTesting();
  public final static List<IBRInstanceType> sizings = Arrays.asList(new AWSSmall(), new AWSMedium(), new AWSLarge(), new AWSGpu());

  public FakeIBRAWSMapper() {
    this(Arrays.asList(new IBRAWSAMISupplier(ibr, sizings)));
  }

  public FakeIBRAWSMapper(List<IBRDialectSupplier> ts) {
    super(ibr, ts);
  }

}
