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

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.util.config.ConfigMap;

public interface IBRInstanceType extends Comparable<IBRInstanceType> {

  default boolean respondsToDialect(String type) {
    return getDialect().equals(type);
  }

  String getDialect();

  String getSize();

  int getOrder();

  String getDialectSpecificIdentifier();

  default IBRInstanceType configure(ConfigMap c) {
    return this;
  }

  @Override
  default int compareTo(IBRInstanceType o) {
    int r = Integer.compare(o.getOrder(), getOrder());
    if (r == 0)
      r = getSize().compareTo(o.getSize());
    return r;
  }

  default Optional<Integer> getMillicentsPerMinute() {
    return empty();
  }

  default List<String> getAliases() {
    return emptyList();
  }

  default boolean matches(String size) {
    return getSize().equals(size) || getAliases().contains(size);
  }
}
