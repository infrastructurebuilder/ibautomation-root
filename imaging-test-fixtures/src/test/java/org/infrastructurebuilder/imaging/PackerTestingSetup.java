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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.ProcessExecution;
import org.infrastructurebuilder.util.ProcessExecutionResult;
import org.infrastructurebuilder.util.ProcessExecutionResultBag;
import org.junit.Before;
import org.junit.BeforeClass;

public class PackerTestingSetup {

  public static final String AMAZON_EBS = "amazon-ebs";
  public static final String SPECIFIC_FIXTURES = "specific-fixtures";
  public static final String ID = "default";
  public static final Optional<Duration> MILLIS_1 = Optional.of(Duration.ofMillis(1));
  public static final Optional<Duration> MINUTES_30 = Optional.of(Duration.ofMinutes(30L));
  public static Path packerExecutable;
  public static final String STDERR = "stdErr";
  public static final List<String> STDERR_LINES = Arrays.asList("GHI", "JKL");
  public static final String STDOUT = "stdOut";
  public static final List<String> STDOUT_LINES = Arrays.asList("ABC", "DEF");
  public static Path TARGET;
  protected static Path targetPath;

  public static Path TEMP;

  public static final String TEST_JSON_JSON = "testJSON.json";
  public static final String TEST_MR_DATA_TXT = "testMRData.txt";
  public static final Optional<Duration> ZERO_DURATION = Optional.of(Duration.ofMinutes(0));

  @BeforeClass
  public static final void beforeClass() throws IOException {
    String target = Optional.ofNullable(System.getProperty("target_dir")).orElse("./target");
    targetPath = Paths.get(target).toRealPath().toAbsolutePath();
    TARGET = Files.createDirectories(targetPath);
    packerExecutable = TARGET.resolve("packer");
  }

  public Path deliver8rFixtures;
  public Path fixtures;
  public Path packerFixtures;
  public ProcessExecution pe;
  public ProcessExecutionResultBag prr;
  public ProcessExecutionResult res;
  public Path stdErr;

  public Path stdOut;
  public Path manifestPath;

  public PackerTestingSetup() {
    super();
  }

  @Before
  public void setup() {
    TEMP = TARGET.resolve(UUID.randomUUID().toString());

    String id = "packer-test-output/" + UUID.randomUUID().toString();
    stdOut = TARGET.resolve(id).resolve(STDOUT);
    stdErr = TARGET.resolve(id).resolve(STDERR);
    manifestPath = TARGET.resolve(id).resolve("manifest.json");
    fixtures = TARGET.resolve("packer-fixtures");
    deliver8rFixtures = fixtures.resolve("specific-fixtures");
    packerFixtures = fixtures.resolve("fixtures");

    pe = new ProcessExecution(ID, "java", Arrays.asList("-version"), Optional.of(Duration.ofDays(1)), stdOut, stdErr,
        Optional.empty(), Optional.empty(), false, Optional.of(new HashMap<>()), Optional.of(targetPath),
        Optional.empty(), Optional.empty(), false);
    res = new ProcessExecutionResult(pe, Optional.of(0), Optional.empty(), Instant.ofEpochMilli(100),
        Duration.ofMillis(100));
  }

}