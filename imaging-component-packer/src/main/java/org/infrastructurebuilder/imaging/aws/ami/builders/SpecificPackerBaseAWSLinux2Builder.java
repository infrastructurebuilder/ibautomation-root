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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.DEFAULT;

import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.IBRDialectMapper;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.infrastructurebuilder.imaging.shell.PackerYumUpdateProvisioner;
import org.infrastructurebuilder.util.auth.IBAuthentication;

@Named(SpecificPackerBaseAWSLinux2Builder.SPECIFIC_AMAZONEBS_BASE_LINUX2)
@Typed(ImageData.class)
public class SpecificPackerBaseAWSLinux2Builder extends SpecificPackerAWSLinux2Builder {
  public static final String SPECIFIC_AMAZONEBS_BASE_LINUX2 = "specific-amazonebs-base-linux2";

  public SpecificPackerBaseAWSLinux2Builder(IBRDialectMapper mapper) {
    super(mapper);
  }

  @Override
  public void addRequiredItemsToFactory(final IBAuthentication a, final PackerFactory f) {
    super.addRequiredItemsToFactory(a, f);
    final PackerProvisioner p = new PackerYumUpdateProvisioner();
    f.addUniqueProvisioner(
        (left, right) -> right.getLookupHint().orElse(DEFAULT).compareTo(left.getLookupHint().orElse(DEFAULT)), p);
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(SPECIFIC_AMAZONEBS_BASE_LINUX2);
  }

}
