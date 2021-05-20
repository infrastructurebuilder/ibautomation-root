/*
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

public class MockIBRBuilderConfigElement implements IBRBuilderConfigElement {

  @Override
  public JSONObject asJSON() {
    return new JSONObject();
  }

  @Override
  public Map<String, Object> get() {
    return new HashMap<String, Object>();
  }

  @Override
  public String getId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String getType() {
    return "fake";
  }

  @Override
  public boolean isTest() {
    return false;
  }

}
