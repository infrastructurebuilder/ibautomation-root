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

import static java.lang.System.out;
import static java.time.Duration.ofMinutes;
import static java.util.Optional.of;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.imaging.maven.PackerManifest.runAndGenerateManifest;
import static org.junit.Assert.assertNotNull;

import java.lang.System.Logger;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class ManifestGeneratorTest extends AbstractPackerFactoryTest {

  private Supplier<PackerManifest> m;

  private final Logger             log = System.getLogger(ManifestGeneratorTest.class.getName());

  @Before
  public void setUp2() throws Exception {
    m = () -> et.withReturningTranslation(() -> runAndGenerateManifest(pf, target.resolve(UUID.randomUUID().toString()),
        "name", "desc", gav, of(ofMinutes(30)), of(out), List.of("--color=false"), Collections.emptyMap()));
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
