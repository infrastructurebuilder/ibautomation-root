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
package org.infrastructurebuilder.maven.ibr;

import static org.infrastructurebuilder.configuration.management.IBRConstants.CONFIG;
import static org.infrastructurebuilder.configuration.management.IBRConstants.ID;
import static org.infrastructurebuilder.configuration.management.IBRConstants.TEST;
import static org.infrastructurebuilder.configuration.management.IBRConstants.TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.infrastructurebuilder.configuration.management.IBRBuilderConfigElement;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

public class DefaultIBRBuilderConfigElement implements IBRBuilderConfigElement {
  private Map<String, Object> config = new HashMap<>();
  private final String        id     = UUID.randomUUID().toString();

  private boolean test = false;
  private String  type;

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()
        // id
        .addString(ID, getId())
        // Type
        .addString(TYPE, getType())
        // Config
        .addJSONObject(CONFIG, new JSONObject(get()))
        // test
        .addBoolean(TEST, isTest()).asJSON();
  }

  @Override
  public Map<String, Object> get() {
    return config;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean isTest() {
    return test;
  }

  public void setConfig(final Map<String, Object> typeConfig) {
    config = typeConfig;
  }

  public void setTest(final boolean test) {
    this.test = test;
  }

  public void setType(final String type) {
    this.type = type;
  }

}
