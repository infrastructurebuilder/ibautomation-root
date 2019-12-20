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
package org.infrastructurebuilder.deployment;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.Objects.*;
import static java.util.Optional.*;

abstract public class AbstractDeployableSupplier implements DeployableSupplier {

  private final Path deployable;
  private final Path rootFile;

  public AbstractDeployableSupplier(Path root, Path rootFile) {
    this.deployable = requireNonNull(root).toAbsolutePath();
    this.rootFile = of(rootFile).map(Path::toAbsolutePath).filter(r -> r.startsWith(this.deployable))
        .map(r -> r.relativize(this.deployable))
        .orElseThrow(() -> new IBDeploymentException("Path " + rootFile + " does not start with " + this.deployable));
  }

  @Override
  public Path get() {
    return this.deployable;
  }

  @Override
  public Path getRootFile() {
    return this.rootFile;
  }

}