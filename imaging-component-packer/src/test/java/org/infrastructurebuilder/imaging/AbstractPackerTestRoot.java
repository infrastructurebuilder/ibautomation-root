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
package org.infrastructurebuilder.imaging;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.junit.Before;
import org.junit.BeforeClass;

public class AbstractPackerTestRoot {

  private static Path root;
  protected static Path target;
  protected static WorkingPathSupplier wps = new WorkingPathSupplier();

  public static Path _getTarget() {
    return target;
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    target = wps.getRoot();
    root = wps.get();

  }

  public static Path getRoot() {
    return root;
  }

  private Path targetDir;

  @Before
  public final void beforeRoot() {
    new PackerConstantsV1() {
    };
    assertNotNull(PackerConstantsV1.TWENTY_SECONDS);
    assertNotNull(PackerConstantsV1.MR_PARAMS);
  }

  public Path getTargetDir() {
    return targetDir;
  }

  @Before
  public final void setUpRoot() throws Exception {
    targetDir = root.resolve("testingWork").resolve(UUID.randomUUID().toString());
    Files.createDirectories(targetDir);
    targetDir = root.relativize(targetDir);
  }
}