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
package org.infrastructurebuilder.configuration.management.shell;

import static java.lang.String.format;
import static java.util.Optional.of;
import static org.infrastructurebuilder.ibr.IBRConstants.SHELL;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRValidationOutput;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;
import org.slf4j.Logger;

@Named(SHELL)
@Typed(IBRValidator.class)
public class DefaultShellIBRValidator extends AbstractShellIBRValidator {
  private static final Logger         log = getLogger(DefaultShellIBRValidator.class);
  private final ShellIBRFileValidator fileValidator;

  @Inject
  public DefaultShellIBRValidator(VersionedProcessExecutionFactory vpef) {
    fileValidator = new ShellIBRFileValidator(vpef);
  }

  @Override
  public Set<IBRValidationOutput> validate(final Path path) {

    final Set<IBRValidationOutput> result = new HashSet<>();
    if (!Files.exists(path)) {

      result.add(new IBRValidationOutput(path, "failed validation",
          of(new IBArchiveException(format("The file doesn't exist : %s", path)))));
      return result;
    }

    try {
      if (fileValidator.checkFile(path)) {
        result.add(new IBRValidationOutput(path, "validated", Optional.empty()));
      } else {
        result.add(new IBRValidationOutput(path, "validation failed",
            of(new IBArchiveException(format("%s: DefaultShellIBRValidator File failed validation", path)))));
        log.error(format("%s: File failed validation", path));
      }
    } catch (final Exception e) {
      result.add(new IBRValidationOutput(path, "validation failed",
          of(new IBArchiveException(format("%s: File failed validation externally", path)))));
      log.error(format("%s: File failed validation externally", path));
    }

    return result;
  }
}
