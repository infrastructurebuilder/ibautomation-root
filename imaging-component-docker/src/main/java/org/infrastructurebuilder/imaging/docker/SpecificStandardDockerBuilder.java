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
package org.infrastructurebuilder.imaging.docker;

import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerSizing;
import org.infrastructurebuilder.imaging.ImageStorage;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.auth.IBAuthentication;

@Named("specific-docker")
@Typed(ImageData.class)
public class SpecificStandardDockerBuilder extends PackerDockerBuilder {

  @Override
  public void addRequiredItemsToFactory(final IBAuthentication a, final PackerFactory f) {

    super.addRequiredItemsToFactory(a, f);
  }

  @Override
  public void updateBuilderWithInstanceData(final PackerSizing size, final IBAuthentication a,
      final Optional<ImageBuildResult> manifest, final List<ImageStorage> disks, final Optional<Type> builderData) {

    super.updateBuilderWithInstanceData(size, a, manifest, disks, builderData);
  }
}
