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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class DefaultM2MTransformer implements M2MTransformer<Object> {

  private Map<String, Responder<TFModel>> map;

  @Inject
  public DefaultM2MTransformer(Map<String, Responder<TFModel>> responderMap) {
    this.map = requireNonNull(responderMap);
  }

  @Override
  public Optional<TFModel> apply(Object t) {
    return map.entrySet().stream().map(e -> e.getValue())
        .map(v -> v.respondTo(t))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Supplier::get)
        .findFirst();
  }

}
