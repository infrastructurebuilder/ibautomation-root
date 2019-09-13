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
package org.infrastructurebuilder.configuration.management.ansible;

import static org.infrastructurebuilder.configuration.management.IBRConstants.ANSIBLE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRValidationOutput;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(ANSIBLE)
@Typed(IBRValidator.class)
public class DefaultAnsibleIBRValidator extends AbstractAnsibleIBRValidator {
  private static final Logger log = LoggerFactory.getLogger(DefaultAnsibleIBRValidator.class);

  private final DefaultAnsibleValidator fileValidator = new DefaultAnsibleValidator();

  @Override
  public Set<IBRValidationOutput> validate(final Path path) {

    final Set<IBRValidationOutput> result = new HashSet<>();
    if (!Files.exists(path)) {
      log.error(String.format("File does not exist : %s", path));
      result.add(new IBRValidationOutput(path, "failed validation",
          Optional.of(new IBArchiveException("The provided file didn't exist"))));
      return result;
    }

    try {
      if (fileValidator.checkFile(path)) {
        result.add(new IBRValidationOutput(path, "validated", Optional.empty()));

      } else {
        result.add(new IBRValidationOutput(path, "validation failed", Optional
            .of(new IBArchiveException("File failed validation; results were logged by DefaultAnsibleValidator"))));

      }
    } catch (final Exception e) {
      result.add(new IBRValidationOutput(path, "validation failed",
          Optional.of(new IBArchiveException("File failed validation externally"))));
      log.error(String.format("%s: File failed validation externally", path));
    }

    return result;
  }
}
