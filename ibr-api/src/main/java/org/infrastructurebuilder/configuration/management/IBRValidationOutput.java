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
import java.util.Optional;

public class IBRValidationOutput implements Comparable<IBRValidationOutput> {

  private final Optional<IBArchiveException> exception;
  private final Path path;
  private final String text;

  public IBRValidationOutput(final Path p, final String valText, final Optional<IBArchiveException> e) {
    text = Objects.requireNonNull(valText);
    path = Objects.requireNonNull(p);
    exception = Objects.requireNonNull(e);
  }

  @Override
  public int compareTo(final IBRValidationOutput o) {
    return getPath().compareTo(o.getPath());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final IBRValidationOutput other = (IBRValidationOutput) obj;
    return path.equals(other.path);
  }

  public Optional<IBArchiveException> getException() {
    return exception;
  }

  public Path getPath() {
    return path;
  }

  public String getValidationText() {
    return text;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + path.hashCode();
    return result;
  }

  public boolean isValid() {
    return !getException().isPresent();
  }
}
