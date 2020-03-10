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
package org.infrastructurebuilder.imaging.aws.ami.sizes;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Named(AWSGpu.SIZE)
@Singleton
public class AWSGpu extends AbstractAWSIBRInstanceType {

  public static final String SIZE = "gpu";

  @Inject
  public AWSGpu() {
    super(SIZE,"p3.2xlarge", 1001);
  }

}
