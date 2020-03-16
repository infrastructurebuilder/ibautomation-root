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

package org.infrastructurebuilder.imaging;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.util.artifacts.Weighted.weighted;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.util.artifacts.WeightedComparator;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.DefaultConfigMapSupplier;

@Named
@Singleton
public class IBRDialectMapper {
  private final List<IBRDialectSupplier> tSupplierList;
  private final AutomationUtils          ibr;

  @Inject
  public IBRDialectMapper(AutomationUtils ibr, List<IBRDialectSupplier> ts) {
    this.ibr = requireNonNull(ibr);
    tSupplierList = requireNonNull(ts).stream().sorted(weighted).collect(toList());
  }

  public Optional<IBRDialect> getDialectFor(String type, Optional<ConfigMapSupplier> cms) {
    // Precompute so that we don't do it for every instance in tSupplierList
    final ConfigMapSupplier c = requireNonNull(cms).orElse(new DefaultConfigMapSupplier());
    return tSupplierList.stream().filter(t -> t.respondsTo(type)).map(t -> t.configure(c)).map(IBRDialectSupplier::get)
        .findFirst(); // Returns highest weighted matching instance
  }
}
