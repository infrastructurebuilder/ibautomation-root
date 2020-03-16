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

import java.util.Optional;
import java.util.function.Supplier;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class JSONIBRExecutionDataReader extends AbstractIBRExecutionDataReader<JSONObjectExecutionData> {

  public JSONIBRExecutionDataReader(String hint) {
    super(hint, "0.0.0", "999.999.999");
  }

  @Override
  public Integer getWeight() {
    return -1;
  }

  @Override
  public Optional<Supplier<JSONObjectExecutionData>> readExecutionData(Xpp3Dom ed) {
    return getExecutionData(ed).map(s -> () -> new JSONObjectExecutionData(s));
  }
}
