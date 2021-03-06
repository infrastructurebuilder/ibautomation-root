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
package org.infrastructurebuilder.ibr.utils;

import static org.infrastructurebuilder.ibr.IBRConstants.IBR_WORKING_PATH_SUPPLIER;

import java.nio.file.Path;

import javax.inject.Inject;

import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TSupplier;

@javax.inject.Named(IBR_WORKING_PATH_SUPPLIER)
@javax.inject.Singleton
public class IBRWorkingPathSupplier extends TSupplier<Path> implements PathSupplier {
  @Inject
  public IBRWorkingPathSupplier() {
    super();
  }

  public IBRWorkingPathSupplier(PathSupplier i) {
    this();
    this.setT(i.get());
  }
}
