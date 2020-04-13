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

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.ProcessExecution;
import org.infrastructurebuilder.util.ProcessExecutionResult;
import org.infrastructurebuilder.util.ProcessExecutionResultBag;
import org.infrastructurebuilder.util.VersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.infrastructurebuilder.util.execution.model.v1_0_0.DefaultProcessExecutionResult;
import org.junit.Before;
import org.junit.BeforeClass;

public class PackerTestingSetup {
  private final static TestingPathSupplier   wps  = new TestingPathSupplier();
  protected VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());

  public static final String             AMAZON_EBS        = "amazon-ebs";
  public static final String             SPECIFIC_FIXTURES = "specific-fixtures";
  public static final String             ID                = "default";
  public static final Optional<Duration> MILLIS_1          = of(ofMillis(1));
  public static final Optional<Duration> MINUTES_30        = of(ofMinutes(30L));
  public static Path                     packerExecutable;
  public static final String             STDERR            = "stdErr";
  public static final List<String>       STDERR_LINES      = asList("GHI", "JKL");
  public static final String             STDOUT            = "stdOut";
  public static final List<String>       STDOUT_LINES      = asList("ABC", "DEF");
  public static Path                     TARGET;
  protected final static Path            targetPath        = wps.getRoot();

  public static Path TEMP;

  public static final String             TEST_JSON_JSON   = "testJSON.json";
  public static final String             TEST_MR_DATA_TXT = "testMRData.txt";
  public static final Optional<Duration> ZERO_DURATION    = of(ofMinutes(0));

  @BeforeClass
  public static final void beforeClass() throws IOException {
    TARGET = Files.createDirectories(targetPath);
    packerExecutable = TARGET.resolve("packer");
  }

  public Path                      deliver8rFixtures;
  public Path                      fixtures;
  public Path                      packerFixtures;
  public ProcessExecution          pe;
  public ProcessExecutionResultBag prr;
  public ProcessExecutionResult    res;
  public Path                      stdErr;

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

    pe = vpef.getDefaultFactory(targetPath, ID, "java").withArguments("-version").withDuration(ofMinutes(1L)).get();

    res = new DefaultProcessExecutionResult(pe, of(0), Optional.empty(), Instant.ofEpochMilli(100), ofMillis(100));
  }

}