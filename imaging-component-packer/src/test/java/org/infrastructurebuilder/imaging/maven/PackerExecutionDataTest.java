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
package org.infrastructurebuilder.imaging.maven;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.CHECKSUM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.imaging.IBRDialectMapperTest;
import org.infrastructurebuilder.util.core.Checksum;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class PackerExecutionDataTest extends IBRDialectMapperTest {

  Checksum checksum;
  Map<String, String> env;
  Instant now;
  PackerExecutionData pe;
  List<String> stdOut;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    stdOut = Arrays.asList("1", "2", "3");
    checksum = new Checksum("abcd");
    now = Instant.now();
    env = new HashMap<>();
    env.put("X", "Y");
    pe = new PackerExecutionData("1.2.3", now, Duration.ofMillis(100), false, Paths.get("/").toString(), checksum,
        Optional.of(env), stdOut, Optional.of(new JSONObject()));

  }

  @Test
  public void testGetDuration() {
    assertEquals(Duration.ofMillis(100), pe.getDuration());
  }

  @Test
  public void testGetMachineReadableLogs() {
    assertNotNull(pe.getMachineReadableLogs());
  }

  @Test
  public void testGetOriginalSource() {
    assertNotNull(pe.getOriginalSource());
  }

  @Test
  public void testGetPackerExecutable() {
    assertNotNull(pe.getPackerExecutable());
  }

  @Test
  public void testGetPackerExecutableChecksum() {
    assertNotNull(pe.getPackerExecutableChecksum());
  }

  @Test
  public void testGetPackerExecutionEnvironment() {
    assertNotNull(pe.getPackerExecutionEnvironment().get());
  }

  @Test
  public void testGetPackerVersion() {
    assertEquals("1.2.3", pe.getPackerVersion().get());
  }

  @Test
  public void testGetStartTime() {
    assertEquals(now, pe.getStartTime());
  }

  @Test
  public void testGetStopTime() {
    assertEquals(now.plus(Duration.ofMillis(100)), pe.getStopTime());
  }

  @Test
  public void testIsTimedOut() {
    assertFalse(pe.isTimedOut());
  }

  @Test
  public void testPackerExecutionData() {
    final JSONObject x = pe.asJSON();
    pe = new PackerExecutionData(x);
    assertNotNull(pe);
    assertNotNull(pe.asChecksum());
  }

  @Test
  public void testPackerExecutionDataBadJSON() {
    final JSONObject x = pe.asJSON();
    x.put(CHECKSUM, checksum.toString());
    pe = new PackerExecutionData(x);
    assertNotNull(pe);
  }

}
