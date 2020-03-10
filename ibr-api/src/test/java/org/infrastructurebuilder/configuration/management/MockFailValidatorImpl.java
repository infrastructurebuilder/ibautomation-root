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
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MockFailValidatorImpl<T> implements IBRValidator<T> {
  Set<IBRValidationOutput> outputs = new HashSet<IBRValidationOutput>();

  @Override
  public boolean respondsTo(final IBRType<T> type) {
    return true;
  }

  @Override
  public Set<IBRValidationOutput> validate(final Path root) {
    outputs.add(
        new IBRValidationOutput(Paths.get("."), "a", Optional.of(new IBArchiveException("fake exception"))));
    outputs.add(new IBRValidationOutput(Paths.get("."), "b", Optional.empty()));
    outputs.add(
        new IBRValidationOutput(Paths.get("."), "c", Optional.of(new IBArchiveException("fake exception"))));
    return outputs;
  }

}
