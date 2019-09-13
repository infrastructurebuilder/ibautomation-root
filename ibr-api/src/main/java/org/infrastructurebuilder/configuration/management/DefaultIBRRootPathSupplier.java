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
package org.infrastructurebuilder.configuration.management;

import java.nio.file.Path;
import java.util.Objects;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultIBRRootPathSupplier implements IBRRootPathSupplier {

  private Path path = null;

  public DefaultIBRRootPathSupplier() {
  }

  @Override
  public Path get() {
    return path;
  }

  @Override
  public IBRRootPathSupplier setPath(final Path path) {
    this.path = Objects.requireNonNull(path);
    return this;
  }

}
