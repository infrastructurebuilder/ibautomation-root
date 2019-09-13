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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Named;

import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRStaticAnalyzer;
import org.infrastructurebuilder.util.DefaultProcessRunner;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.ProcessExecutionResult;
import org.infrastructurebuilder.util.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("shell-ibr-validator")
public class ShellIBRFileValidator {
  private static final Logger log = LoggerFactory.getLogger(ShellIBRFileValidator.class);
  private Path targetPath = null;

  public boolean checkFile(final Path path) throws IBArchiveException {
    return IBArchiveException.et.withReturningTranslation(() -> {
      return check(IBUtils.readFile(path));
    });
  }

  private boolean check(final String string) throws IBArchiveException {

    final IBRStaticAnalyzer defaultDisallowedStrings = new IBRStaticAnalyzer() {
      @Override
      public Logger getLog() {
        return LoggerFactory.getLogger(this.getClass());
      }
    };
    if (!defaultDisallowedStrings.isValid(string)) {
      log.error("IBRStaticAnalyzer in Shell code");
      return false;
    }

    return IBArchiveException.et.withReturningTranslation(() -> {

      final Path tempDirectory = Paths.get(getTargetPath().toString(), UUID.randomUUID().toString());

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
          log.info(line);
        });
        res.getStdErr().stream().forEach(line -> {
          log.error(line);
        });
        if (resultCode.get() != 0) {
          log.error(String.format("Nonzero Result code %d", resultCode.get()));
          return false;
        }
      }
      return true;
    });
  }

  private Map<String, ProcessExecutionResult> executeAgainst(final String shellFileName) throws Exception {
    final List<String> args = Arrays.asList("-n", shellFileName);
    final Path scratchDirectory = Paths.get(getTargetPath().toString(), UUID.randomUUID().toString());
    Files.createDirectories(scratchDirectory.getParent());
    try (ProcessRunner pr = new DefaultProcessRunner(scratchDirectory, Optional.empty(), Optional.of(log),
        Optional.empty())) {
      return IBArchiveException.et.withReturningTranslation(() -> pr.addExecution(UUID.randomUUID().toString(), "bash",
          args, Optional.empty(), Optional.empty(), Optional.of(getTargetPath()), Optional.empty(), false,
          Optional.empty(), Optional.empty(), Optional.empty(), false).lock().get().get().getResults());
    }
  }

  private Path getTargetPath() throws IBArchiveException {
    if (targetPath == null) {
      IBArchiveException.et.withTranslation(() -> {
        targetPath = Paths.get(Optional.ofNullable(System.getProperty("target_directory")).orElse("./target"));
      });
    }
    return targetPath;
  }

}
