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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A Responder provides an Optional Supplier of type M for a given object.
 * The supplied object might be invalid or useless, in which case the
 * supplier should return Optional.empty()
 *
 * @author mykel.alvis
 *
 * @param <M>
 */
public interface Responder<M> {
  /**
   *
   * @param object Tested object
   * @return non-empty if the implementation of this interface "responds to" the object
   */
  Optional<Supplier<M>> respondTo(Object object);

}
