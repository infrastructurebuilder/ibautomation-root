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
import static java.util.stream.Collectors.toMap;
import static org.infrastructurebuilder.configuration.management.shell.ShellConstants.SHELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRValidationOutput;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.infrastructurebuilder.configuration.management.shell.DefaultShellIBRValidator;
import org.infrastructurebuilder.util.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.VersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDefaultShellIBRValidator {
  public final static Logger               log  = LoggerFactory.getLogger(TestDefaultShellIBRValidator.class);
  private final static TestingPathSupplier wps  = new TestingPathSupplier();
  private VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());

  private Path         target;
  private IBRValidator test;
  Path                 fileA;
  Path                 fileB;
  Path                 fileC;
  Path                 fileD;
  Path                 fileE;
  Path                 fileF;
  Path                 fileG;
  Path                 fileH;
  Path                 targetEmptyDirectory;

  Path targetEmptySubfolder;
  Path targetPath;
  Path targetSneakyNotDirectory;

  @After
  public void after() {
    fileH.toFile().setReadable(true);
    for (final Path p : Arrays.asList(targetPath, targetEmptyDirectory, targetEmptySubfolder,
        targetSneakyNotDirectory)) {
      IBUtils.deletePath(p);
    }
  }

  @Before
  public void beforeClass() throws IOException, PlexusContainerException, ComponentLookupException {
    target = wps.getRoot();
    targetPath = vpef.getScratchDir();
    targetEmptyDirectory = target.resolve(UUID.randomUUID().toString());
    targetEmptySubfolder = target.resolve(UUID.randomUUID().toString());
    targetSneakyNotDirectory = target.resolve(UUID.randomUUID().toString());
    final Path targetEmptySubfolderShell = targetEmptySubfolder.resolve(SHELL);
    final Path shellPath = targetPath.resolve(SHELL);
    Files.createDirectories(shellPath);
    final Path subDir = shellPath.resolve("subdir");
    final Path subDir2 = subDir.resolve("subdir2");
    final Path subDir3 = subDir2.resolve("subdir3");

    fileA = shellPath.resolve("a.sh");
    fileB = shellPath.resolve("b.sh");

    fileC = Paths.get(shellPath.toString(), "/c.sh");
    fileD = Paths.get(subDir.toString(), "/d.sh");
    fileE = Paths.get(subDir.toString(), "/e.sh");
    fileF = Paths.get(subDir3.toString(), "/f.sh");
    fileG = Paths.get(subDir3.toString(), "/g.sh");
    fileH = Paths.get(subDir3.toString(), "/h.sh");
    final Path fileSneakyMasquerader = Paths.get(targetSneakyNotDirectory.toString());
    FileUtils.copyDirectoryToDirectory(target.resolve("test-classes").resolve(SHELL).toFile(), targetPath.toFile());
    for (final Path p : Arrays.asList(targetEmptyDirectory, targetEmptySubfolder, targetEmptySubfolderShell)) {
      Files.createDirectories(p);
    }
    Files.write(fileSneakyMasquerader, "".getBytes());
    fileH.toFile().setReadable(false);
    test = new DefaultShellIBRValidator(vpef);
  }

  @Test
  public void testDirectoryValidatorAlternate() {

    final Map<Path, Optional<IBArchiveException>> result = Arrays
        .asList(fileA, fileB, fileC, fileD, fileE, fileF, fileG, fileH).stream()
        // Map to validation results
        .flatMap(t -> test.validate(t).stream())
        // Fetch result map
        .collect(toMap(IBRValidationOutput::getPath, IBRValidationOutput::getException));

    assertEquals("Map size should be 8", 8, result.size());
    if (result.get(fileA).isPresent()) {
      log.debug("fileA Message " + result.get(fileA).get().getMessage());
    }
    assertFalse("Good file should have empty exception list", result.get(fileA).isPresent());
    for (final Path a : Arrays.asList(fileB, fileC, fileD, fileE, fileF, fileG, fileH)) {
      assertTrue("Bad file should be present in list " + a, result.containsKey(a));
      assertTrue("Bad file should have exception " + a, result.get(a).isPresent());
    }
  }

  @Test
  public void testEmptyDirectoryValidator() {
    final Set<IBRValidationOutput> result = test.validate(targetEmptyDirectory);
    assertEquals("Result size should be one", 1, result.size());
    result.forEach(output -> {
      assertFalse("Empty directory should have an exception", output.isValid());
    });
  }

  @Test
  public void testEmptySubfolderValidator() {
    final Set<IBRValidationOutput> result = test.validate(targetEmptySubfolder);
    assertEquals("Result size should be one", 1, result.size());
    result.forEach(output -> {
      assertFalse("Empty directory should have an exception", output.isValid());
    });
  }

  @Test
  public void testNonexistentDirectoryValidator() {
    final Path nonexistent = Paths.get("/non/existent/path");
    final Set<IBRValidationOutput> result = test.validate(nonexistent);
    result.forEach(output -> {
      assertFalse("Nonexistent directory should fail validation", output.isValid());
    });
  }
}
