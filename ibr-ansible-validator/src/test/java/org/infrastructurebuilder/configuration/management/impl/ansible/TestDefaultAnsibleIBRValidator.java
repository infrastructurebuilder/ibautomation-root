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
package org.infrastructurebuilder.configuration.management.impl.ansible;

import static java.util.Optional.empty;
import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE;
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
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRValidationOutput;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleIBRValidator;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleValidator;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.util.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.VersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultAnsibleIBRValidator {
  private final static TestingPathSupplier wps  = new TestingPathSupplier();
  private VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());
  private AutomationUtils                  ibr  = new AutomationUtilsTesting();
  private Path                             target;
  private IBRValidator                     test;
  PlexusContainer                          container;
  Path                                     fileA;
  Path                                     fileB;
  Path                                     fileC;
  Path                                     fileD;
  Path                                     fileE;
  Path                                     fileF;
  Path                                     fileG;
  Path                                     fileH;

  Path targetEmptyDirectory;
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
    final Path targetEmptySubfolderAnsible = targetEmptySubfolder.resolve(ANSIBLE);
    final Path ansiblePath = targetPath.resolve(ANSIBLE);
    Files.createDirectories(ansiblePath);
    final Path subDir = ansiblePath.resolve("subdir");
    final Path subDir2 = subDir.resolve("subdir2");
    final Path subDir3 = subDir2.resolve("subdir3");

    fileA = ansiblePath.resolve("a.yml");
    fileB = ansiblePath.resolve("b.yml");

    fileC = Paths.get(ansiblePath.toString(), "/c.yml");
    fileD = Paths.get(subDir.toString(), "/d.yml");
    fileE = Paths.get(subDir.toString(), "/e.yml");
    fileF = Paths.get(subDir3.toString(), "/f.yml");
    fileG = Paths.get(subDir3.toString(), "/g.yml");
    fileH = Paths.get(subDir3.toString(), "/h.yml");
    final Path fileSneakyMasquerader = Paths.get(targetSneakyNotDirectory.toString());
    FileUtils.copyDirectoryToDirectory(target.resolve("test-classes").resolve(ANSIBLE).toFile(), targetPath.toFile());
    for (final Path p : Arrays.asList(targetEmptyDirectory, targetEmptySubfolder, targetEmptySubfolderAnsible)) {
      Files.createDirectories(p);
    }
    Files.write(fileSneakyMasquerader, "".getBytes());
    fileH.toFile().setReadable(false);
    test = new DefaultAnsibleIBRValidator(ibr, new DefaultAnsibleValidator(vpef));
  }

  @Test
  public void testDirectoryValidatorAlternate() {

    final Map<Path, Optional<IBArchiveException>> result = Arrays
        .asList(fileA, fileB, fileC, fileD, fileE, fileF, fileG, fileH).stream().flatMap(t -> test.validate(t).stream())
        .collect(Collectors.toMap(k -> k.getPath(), v -> v.getException()));

    assertEquals("Map size should be 8", 8, result.size());
    Optional<IBArchiveException> res = result.get(fileA);
    if (res.isPresent()) {

    }
    assertFalse("Good file should have empty exception list", res.isPresent());
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
  public void testFileMasqueradingAsDirectoryFailsValidation() {
    final Set<IBRValidationOutput> result = test.validate(targetSneakyNotDirectory);
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
