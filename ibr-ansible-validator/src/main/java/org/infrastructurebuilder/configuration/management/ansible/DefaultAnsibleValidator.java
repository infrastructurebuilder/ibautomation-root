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
package org.infrastructurebuilder.configuration.management.ansible;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.delete;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.infrastructurebuilder.configuration.management.IBArchiveException.et;
import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE_DRY_RUN_DISABLE_PATTERN;
import static org.infrastructurebuilder.util.core.IBUtils.writeString;

import java.lang.System.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRStaticAnalyzer;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.executor.DefaultProcessRunner;
import org.infrastructurebuilder.util.executor.ProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.ProcessExecutionResult;
import org.infrastructurebuilder.util.executor.ProcessRunner;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;

@Named("ansible-ibr-validator")
@Typed(DefaultAnsibleValidator.class)
public class DefaultAnsibleValidator {
  private static final Logger                    log        = System.getLogger(DefaultAnsibleValidator.class.getName());
  private Path                                   targetPath = null;
  private final TestingPathSupplier              wps        = new TestingPathSupplier();
  private final VersionedProcessExecutionFactory vpef;

  @Inject
  public DefaultAnsibleValidator(VersionedProcessExecutionFactory v) {
    this.vpef = requireNonNull(v);
  }

  public boolean checkFile(final Path path) throws IBArchiveException {
    return et.withReturningTranslation(() -> {
      return check(IBUtils.readFile(path));
    });
  }

  private boolean check(final String string) throws IBArchiveException {

    final Matcher m = ANSIBLE_DRY_RUN_DISABLE_PATTERN.matcher(string);
    if (m.find()) {
      log.log(Logger.Level.ERROR,String.format("Disallowed: check_mode yes at index %d", m.start()));
      return false;
    }

    final IBRStaticAnalyzer defaultDisallowedStrings = new IBRStaticAnalyzer() {
      @Override
      public Logger getLog() {
        return System.getLogger(this.getClass().getName());
      }
    };
    if (!defaultDisallowedStrings.isValid(string)) {
      log.log(Logger.Level.ERROR,"Validation failed");
      return false;
    }

    final String inventoryString = "[default]\r\n127.0.0.1";

    return et.withReturningTranslation(() -> {

      final Path tempDirectory = Paths.get(getTargetPath().toString(), randomUUID().toString());

      final Path tempPlaybookFile = Paths.get(tempDirectory.toString(), "playbook.yml");
      final Path tempInventoryFile = Paths.get(tempDirectory.toString(), "inventory.yml");

      createDirectories(tempDirectory);

      createFile(tempPlaybookFile);
      createFile(tempInventoryFile);

      writeString(tempPlaybookFile, string);
      writeString(tempInventoryFile, inventoryString);

      final Map<String, ProcessExecutionResult> resultMap = executeAgainst(
          tempPlaybookFile.toAbsolutePath().toRealPath().toString(),
          tempInventoryFile.toAbsolutePath().toRealPath().toString());

      delete(tempPlaybookFile);
      delete(tempInventoryFile);
      for (final ProcessExecutionResult res : resultMap.values()) {
        final Optional<Integer> resultCode = res.getResultCode();
        res.getStdOut().stream().forEach(line -> {
          log.log(Logger.Level.INFO,line);
        });
        res.getStdErr().stream().forEach(line -> {
          log.log(Logger.Level.ERROR,line);
        });
        if (resultCode.get() != 0) {
          log.log(Logger.Level.ERROR,String.format("Nonzero Result code %d", resultCode.get()));
          return false;
        }
      }
      return true;
    });
  }

  private Map<String, ProcessExecutionResult> executeAgainst(final String playbookFileName,
      final String inventoryFileName) throws Exception {
    final String[] args = {playbookFileName, "--syntax-check", "-i", inventoryFileName};
    final Path scratchDirectory = Paths.get(getTargetPath().toString(), randomUUID().toString());
    createDirectories(scratchDirectory.getParent());
    try (ProcessRunner pr = new DefaultProcessRunner(scratchDirectory, empty(), of(log), empty())) {
      ProcessExecutionFactory pef = vpef.getDefaultFactory(getTargetPath(), randomUUID().toString(), "ansible-playbook").withArguments(args);
      return et.withReturningTranslation(
          () -> pr.add(pef).lock()
              .get().get().getResults());
    }
  }

  private Path getTargetPath() throws IBArchiveException {
    return ofNullable(targetPath).orElse(wps.getRoot());
  }

}
