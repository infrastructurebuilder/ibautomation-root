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

import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.IBRDialectMapper;
import org.infrastructurebuilder.imaging.ImageData;

@Named(SpecificPackerAWSLinux2Builder.SPECIFIC_AMAZONEBS_LINUX2)
@Typed(ImageData.class)
public class SpecificPackerAWSLinux2Builder extends SpecificPackerAWSBuilder {
  public static final String SPECIFIC_AMAZONEBS_LINUX2 = "specific-amazonebs-linux2";

  public SpecificPackerAWSLinux2Builder(IBRDialectMapper mapper) {
    super(mapper);
  }
  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(SPECIFIC_AMAZONEBS_LINUX2);
  }

  @Override
  String getSourceFilterName() {
    return DEFAULT_AMZN2_LINUX_AMI_STRING;
  }

}
