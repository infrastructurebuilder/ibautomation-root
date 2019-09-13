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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import org.infrastructurebuilder.configuration.management.IBRDataObject;
import org.json.JSONObject;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.DefaultIBConfigSupplier;
import org.junit.Before;
import org.junit.Test;

public class TestCMCompileDAO {
  private IBRDataObject<JSONObject> test;
  private DummyIBRType type;
  private final Map<String, Object> configMap = new HashMap<String, Object>();

  @Before
  public void setupBefore() {
    DummyIBRType.getRps().setPath(Paths.get("."));
    type = new DummyIBRType();

    configMap.put("file", "path/to/file");
  }

  @Test
  public void testCMCompileDAO() {
    type.setConfigSupplier(new DefaultIBConfigSupplier().setConfig(configMap));
    test = new IBRDataObject<>(type, Paths.get("."), new MockIBRBuilderConfigElement());
    assertNotNull(test.getBuilder());
    assertNotNull(test.getCount());
    assertNotNull(test.getName());
    assertNotNull(test.getOutput());
    assertNotNull(test.getTargetPath());
  }

  @Test(expected = IBArchiveException.class)
  public void testFailedCMCompileDAO() {
    type = new DummyIBRType(new TreeSet<>(), new HashSet<>(Arrays.asList(new MockFailValidatorImpl<JSONObject>())));
    type.setConfigSupplier(new DefaultIBConfigSupplier().setConfig(configMap));
    test = new IBRDataObject<>(type, Paths.get("."), new MockIBRBuilderConfigElement());
  }

  @Test
  public void testNotFailedCMCompileDAO() {
    type = new DummyIBRType(new TreeSet<>(), new HashSet<>(Arrays.asList(new MockValidatorImpl())));
    type.setConfigSupplier(new DefaultIBConfigSupplier().setConfig(configMap));
    test = new IBRDataObject<>(type, Paths.get("."), new MockIBRBuilderConfigElement());
  }

  @Test
  public void testGetArchiveSubpath() {
    type = new DummyIBRType(new TreeSet<>(), new HashSet<>(Arrays.asList(new MockFailValidatorImpl<JSONObject>())));
    assertEquals(Paths.get("fake"), type.getArchiveSubPath());
    assertNotNull(type.getRoot());
    final String t = type.getId();
    assertNotNull(t);
    type.setId(UUID.randomUUID().toString());
    assertNotEquals(t, type.getId());
  }
}
