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
package org.infrastructurebuilder.automation;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.json.JSONObject;
import org.json.XML;

public class JSONSpecificExecution extends DefaultSpecificExecution {

  private final JSONObject json;

  public JSONSpecificExecution(Xpp3Dom d, List<String> preKeys) {
    super(d);
    JSONObject j = XML.toJSONObject(d.toString());
    requireNonNull(preKeys).forEach(j::remove);
    this.json = j;
  }

}
