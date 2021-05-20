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
package org.infrastructurebuilder.configuration.management.shell;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;
import static org.infrastructurebuilder.configuration.management.IBArchiveException.et;

import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRStaticAnalyzer;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.executor.DefaultProcessRunner;
import org.infrastructurebuilder.util.executor.ProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.ProcessExecutionResult;
import org.infrastructurebuilder.util.executor.ProcessRunner;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;


@Named("shell-ibr-validator")
public class ShellIBRFileValidator {
  private static final Logger                    log        = System.getLogger(ShellIBRFileValidator.class.getName());
  private Path                                   targetPath = null;
  private final VersionedProcessExecutionFactory vpef;

  @Inject
  public ShellIBRFileValidator(VersionedProcessExecutionFactory vpef) {
    this.vpef = Objects.requireNonNull(vpef);
  }

  public boolean checkFile(final Path path) throws IBArchiveException {
    return et.withReturningTranslation(() -> {
      return check(IBUtils.readFile(path));
    });
  }

  private boolean check(final String string) throws IBArchiveException {

    final IBRStaticAnalyzer defaultDisallowedStrings = new IBRStaticAnalyzer() {
      @Override
      public Logger getLog() {
        return System.getLogger(this.getClass().getName());
      }
    };
    if (!defaultDisallowedStrings.isValid(string)) {
      log.log(Logger.Level.ERROR,"IBRStaticAnalyzer in Shell code");
      return false;
    }

    return et.withReturningTranslation(() -> {

      final Path tempDirectory = Paths.get(getTargetPath().toString(), randomUUID().toString());

      final Path tempShellFile = Paths.get(tempDirectory.toString(), "shell.sh");

      Files.createDirectories(tempDirectory);

      Files.createFile(tempShellFile);

      IBUtils.writeString(tempShellFile, string);

      final Map<String, ProcessExecutionResult> resultMap = executeAgainst(
          tempShellFile.toAbsolutePath().toRealPath().toString());

      Files.delete(tempShellFile);
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

  private Map<String, ProcessExecutionResult> executeAgainst(final String shellFileName) throws Exception {
    final String[] args = { "-n", shellFileName };
    final Path scratchDirectory = getTargetPath().resolve(randomUUID().toString());
    Files.createDirectories(scratchDirectory.getParent());
    try (ProcessRunner pr = new DefaultProcessRunner(scratchDirectory, empty(), of(log), empty())) {
      ProcessExecutionFactory pef = vpef.getDefaultFactory(getTargetPath(), randomUUID().toString(), "bash")
          .withArguments(args);
      return et.withReturningTranslation(() -> pr.add(pef)
          // Execute
          .lock()
          // Fetch ResultsBag from optional supplier
          .get().get()
          // Fetch results
          .getResults());
    }
  }

  private Path getTargetPath() throws IBArchiveException {
    if (targetPath == null) {
      et.withTranslation(() -> {
        targetPath = Paths.get(Optional.ofNullable(System.getProperty("target_directory")).orElse("./target"));
      });
    }
    return targetPath;
  }

}
