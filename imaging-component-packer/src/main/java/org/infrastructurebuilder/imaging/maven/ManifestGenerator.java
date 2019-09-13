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
package org.infrastructurebuilder.imaging.maven;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_COMMAND;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_EXECUTION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TWENTY_SECONDS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.VERSION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.VERSION_PARAM;
import static org.infrastructurebuilder.imaging.PackerException.et;
import static org.infrastructurebuilder.util.IBUtils.readFile;
import static org.infrastructurebuilder.util.IBUtils.readJsonObject;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.codehaus.plexus.PlexusContainer;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.util.DefaultProcessRunner;
import org.infrastructurebuilder.util.ProcessExecutionResult;
import org.infrastructurebuilder.util.ProcessExecutionResultBag;
import org.infrastructurebuilder.util.ProcessRunner;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.json.JSONObject;

public class ManifestGenerator implements Supplier<PackerManifest> {

  static PackerManifest runAndGenerateManifest(final Path packerFile, final PackerFactory<JSONObject> factory, final Path tempDir,
      final String name, final String desc, final GAV coords, final Optional<Duration> timeOut,
      final Optional<PrintStream> sOut, final List<String> params, final Map<String, String> runtime) throws Exception {
    factory.getLog().info("Running packer for " + packerFile);
    final PlexusContainer c = factory.getContainer();
    final Path packer = factory.getPackerExecutable();
    final JSONObject packerFileAsJSON = readJsonObject(packerFile);
    final List<String> ver = new ArrayList<>(Arrays.asList(VERSION_PARAM));
    final List<String> buildIt = new ArrayList<>(Arrays.asList(BUILD_COMMAND));
    buildIt.addAll(params);
    buildIt.add(packerFile.toAbsolutePath().toString());
    final Optional<Path> stdIn = Optional.empty();
    final Map<String, String> packerEnvParams = new HashMap<>();
    packerEnvParams.putAll(runtime);
    try (ProcessRunner pr = new DefaultProcessRunner(

        tempDir.resolve(UUID.randomUUID().toString()),

        sOut,

        Optional.ofNullable(factory.getLog()),

        factory.getMetaRoot())) {

      pr.addExecution(VERSION, packer.toString(), ver, TWENTY_SECONDS, stdIn, Optional.of(factory.getRoot()),
          factory.getPackerChecksum(), false, Optional.of(packerEnvParams), factory.getMetaRoot(), Optional.empty(),
          false)

          .addExecution(PACKER_EXECUTION, packer.toString(), buildIt, timeOut, stdIn, Optional.of(factory.getRoot()),
              factory.getPackerChecksum(), false, Optional.of(packerEnvParams), factory.getMetaRoot(), Optional.empty(),
              false)

          .setKeepScratchDir(factory.getLog().isDebugEnabled());
      pr

          .lock();
      final ProcessExecutionResultBag vbb = pr.get().orElseThrow(() -> new PackerException("Unable to run packer "));
      final String versionString;
      final ProcessExecutionResult res = vbb.getExecution(VERSION)
          .orElseThrow(() -> new PackerException("no version execution"));
      if (res.isError())
        throw new PackerException("Failed to get 'version' result",
            res.getException().orElse(new PackerException("internal")));
      else {
        versionString = res.getStdOut().iterator().next();
      }
      if (!Files.exists(factory.getManifestTargetPath()))
        throw new PackerException("Manifest target file does not exist " + factory.getManifestTargetPath());
      return vbb.getExecution(PACKER_EXECUTION).map(packVbb -> {
        final JSONObject manifestJSON = et
            .withReturningTranslation(() -> new JSONObject(readFile(factory.getManifestTargetPath())));
        final PackerExecutionData e = new PackerExecutionData(versionString, Instant.now(), packVbb.getRunningtime(),
            packVbb.isTimedOut(), packer.toString(), factory.getPackerChecksum().get(),
            Optional.of(packVbb.getExecutionEnvironment()), packVbb.getStdOut(), Optional.of(packerFileAsJSON));

        return new PackerManifest(e, coords, manifestJSON, name, desc, c, factory);
      })

          .orElseThrow(() -> new PackerException("No " + PACKER_EXECUTION + " result was available"));

    }
  }

  private final GAV coords;
  private final String desc;
  private final PackerFactory<JSONObject> factory;
  private final String name;
  private final Path packerFile;
  private final List<String> params;
  private final Map<String, String> runtime;
  private final Optional<PrintStream> sOut;
  private final Path tempDir;
  private final Optional<Duration> timeOut;

  public ManifestGenerator(final PackerFactory<JSONObject> factory, final Path tempDir, final String name, final String desc,
      final GAV coords, final Optional<Duration> timeOut, final Optional<PrintStream> sOut, final List<String> params,
      final Map<String, String> runtime) {
    this.factory = factory;
    this.tempDir = tempDir;
    this.name = name;
    this.desc = desc;
    this.coords = coords;
    this.timeOut = timeOut;
    this.sOut = sOut;
    this.params = params;
    this.runtime = runtime;
    packerFile = factory.get().orElseThrow(() -> new PackerException("Could not generate packerfile"));
  }

  @Override
  public PackerManifest get() {
    return PackerException.et.withReturningTranslation(
        () -> runAndGenerateManifest(packerFile, factory, tempDir, name, desc, coords, timeOut, sOut, params, runtime));
  }

}
