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

import static org.infrastructurebuilder.configuration.management.IBRConstants.SHELL;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(SHELL)
@Typed(IBRValidator.class)
public class DefaultShellIBRValidator extends AbstractShellIBRValidator {
  private static final Logger log = LoggerFactory.getLogger(DefaultShellIBRValidator.class);
  private final ShellIBRFileValidator fileValidator = new ShellIBRFileValidator();

  @Inject
  public DefaultShellIBRValidator() {
  }

  @Override
  public Set<IBRValidationOutput> validate(final Path path) {

    final Set<IBRValidationOutput> result = new HashSet<>();
    if (!Files.exists(path)) {

      result.add(new IBRValidationOutput(path, "failed validation",
          Optional.of(new IBArchiveException(String.format("The file doesn't exist : %s", path)))));
      return result;
    }

    try {
      if (fileValidator.checkFile(path)) {
        result.add(new IBRValidationOutput(path, "validated", Optional.empty()));
      } else {
        result.add(new IBRValidationOutput(path, "validation failed", Optional
            .of(new IBArchiveException(String.format("%s: DefaultShellIBRValidator File failed validation", path)))));
        log.error(String.format("%s: File failed validation", path));
      }
    } catch (final Exception e) {
      result.add(new IBRValidationOutput(path, "validation failed",
          Optional.of(new IBArchiveException(String.format("%s: File failed validation externally", path)))));
      log.error(String.format("%s: File failed validation externally", path));
    }

    return result;
  }
}
