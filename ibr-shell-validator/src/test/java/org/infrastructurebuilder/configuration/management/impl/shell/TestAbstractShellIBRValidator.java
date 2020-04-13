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
package org.infrastructurebuilder.configuration.management.impl.shell;

import static java.util.Optional.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.configuration.management.AbstractIBRType;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.configuration.management.shell.AbstractShellIBRValidator;
import org.infrastructurebuilder.configuration.management.shell.DefaultShellIBRValidator;
import org.infrastructurebuilder.configuration.management.shell.ShellIBRType;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.util.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.VersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.json.JSONObject;
import org.junit.Test;

public class TestAbstractShellIBRValidator {
  private final static TestingPathSupplier wps  = new TestingPathSupplier();
  private VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());

  private class TestIBRType extends AbstractIBRType {
    public TestIBRType() {
      super(new AutomationUtilsTesting(), Arrays.asList(new DefaultShellIBRValidator(vpef)));
    }

    @Override
    public JSONObject transformToProvisionerEntry(final String typeName, final Path root, final Path targetFile,
        final Optional<IBArchive> archive, final List<ImageData> builders) {
      return new JSONObject().put("name", "test");
    }

  }

  @Test
  public void testTypes() {
    final AbstractShellIBRValidator validator = new DefaultShellIBRValidator(vpef);
    assertTrue(validator
        .respondsTo(new ShellIBRType(new AutomationUtilsTesting(), Arrays.asList(new DefaultShellIBRValidator(vpef)))));
    assertFalse(validator.respondsTo(new TestIBRType()));
  }
}
