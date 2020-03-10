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

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.json.JSONObject;

public class PathMapEntry implements JSONOutputEnabled {
  private final String b;
  private final Path   p;

  public PathMapEntry(String b, Path p) {
    this.b = requireNonNull(b);
    this.p = requireNonNull(p);
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject().put(this.b, this.p.toString());
  }

  public String getKey() {
    return b;
  }

  public Path getPath() {
    return p;
  }

}
