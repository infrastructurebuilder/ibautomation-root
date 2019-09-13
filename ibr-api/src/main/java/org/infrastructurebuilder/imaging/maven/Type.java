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
package org.infrastructurebuilder.imaging.maven;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;

public class Type {
  private Map<String, String> extra;
  private String hint;
  private DefaultGAV parent;

  public Optional<Map<String, String>> getExtra() {
    return Optional.ofNullable(extra);
  }

  public String getHint() {
    return Optional.ofNullable(hint).orElseThrow(() -> new PackerException("No hint available"));
  }

  public Optional<DefaultGAV> getParent() {
    return Optional.ofNullable(parent);
  }

  public void setExtra(final Map<String, String> extra) {
    this.extra = extra;
  }

  public void setHint(final String hint) {
    this.hint = Objects.requireNonNull(hint);
  }

  public void setParent(final DefaultGAV parent) {
    this.parent = parent;
  }

}
