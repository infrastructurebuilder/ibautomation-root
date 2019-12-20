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
package org.infrastructurebuilder.deployment.tf;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.deployment.DeployableSupplier;

/**
 * Supply a path to a deployable that is specifically meant for Terraform
 * (and is thusly formatted)
 * @author mykel.alvis
 *
 */
public class TFDeployableSupplier implements DeployableSupplier {

  private final Path root;
  private final Path rel;

  public TFDeployableSupplier(Path root, Path relative) {
    this.root = requireNonNull(root);
    if (requireNonNull(relative).isAbsolute())
      throw new IBException("Relative path must be relative");
    this.rel = requireNonNull(relative);
  }

  @Override
  public Path get() {
    return this.rel;
  }

  @Override
  public Path getRootFile() {
    return root;
  }

}