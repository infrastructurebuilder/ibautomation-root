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

import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestGeneratorTest extends AbstractPackerFactoryTest {

  private ManifestGenerator m;

  private List<String> params;
  private Map<String, String> runtime;
  private Path tempDir;
  Logger log = LoggerFactory.getLogger(ManifestGeneratorTest.class);

  @Before
  public void setUp2() throws Exception {
    tempDir = target.resolve(UUID.randomUUID().toString());
    params = Arrays.asList("--color=false");
    runtime = new HashMap<>();
    m = new ManifestGenerator(pf, tempDir, "name", "desc", gav, Optional.of(Duration.ofMinutes(30)),
        Optional.of(System.out), params, runtime);
  }

  @Test
  public void testGet() {
    assertNotNull(m.get());
  }

  @Override
  Logger getLog() {
    return log;
  }

}
