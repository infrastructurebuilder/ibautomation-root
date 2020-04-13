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

import static java.nio.file.Files.exists;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_COMMAND;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.DESCRIPTION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.GAV_KEY;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.LAST_RUN_UUID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.MERGED_MANIFEST;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_AUTH_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_MANIFEST;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_EXECUTION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TWENTY_SECONDS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.VERSION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.VERSION_PARAM;
import static org.infrastructurebuilder.util.IBUtils.asJSONObjectStream;
import static org.infrastructurebuilder.util.IBUtils.getOptString;
import static org.infrastructurebuilder.util.IBUtils.readJsonObject;

import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.codehaus.plexus.PlexusContainer;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.IBRHintMap;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.util.DefaultProcessRunner;
import org.infrastructurebuilder.util.ProcessExecutionFactory;
import org.infrastructurebuilder.util.ProcessExecutionResultBag;
import org.infrastructurebuilder.util.ProcessRunner;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONArray;
import org.json.JSONObject;

public class PackerManifest implements JSONOutputEnabled {

  static PackerManifest runAndGenerateManifest(final PackerFactory factory, final Path tempDir, final String name,
      final String desc, final GAV coords, final Optional<Duration> timeOut, final Optional<PrintStream> sOut,
      final List<String> params, final Map<String, String> runtime) throws Exception {
    final Path packerFile = factory.get();
    factory.getLog().info("Running packer for " + packerFile);
    final PlexusContainer c = factory.getContainer();
    final Path packer = factory.getPackerExecutable();
    final JSONObject packerFileAsJSON = readJsonObject(packerFile);
    final List<String> buildIt = new ArrayList<>(asList(BUILD_COMMAND));
    buildIt.addAll(params);
    buildIt.add(packerFile.toAbsolutePath().toString());
    String[] args = buildIt.toArray(new String[buildIt.size()]);
    final Map<String, String> packerEnvParams = new HashMap<>();
    packerEnvParams.putAll(runtime);
    try (ProcessRunner pr = new DefaultProcessRunner(
        // Rando working dir
        tempDir.resolve(UUID.randomUUID().toString()),
        // std out
        sOut,
        // Potential log
        ofNullable(factory.getLog()),
        // Top level working dir for factory
        of(factory.getMetaRoot()))) {

      ProcessExecutionFactory pef = factory.getProcessExecutionFactory(VERSION).withArguments(VERSION_PARAM)
          .withDuration(TWENTY_SECONDS).withEnvironment(packerEnvParams);

      pr.add(pef);

      ProcessExecutionFactory exec = factory.getProcessExecutionFactory(PACKER_EXECUTION).withArguments(args)
          .withEnvironment(packerEnvParams).withDuration(timeOut.orElse(Duration.ZERO));
      pr.add(exec);

      pr.setKeepScratchDir(factory.getLog().isDebugEnabled());
      pr.lock();
      final ProcessExecutionResultBag vbb = pr.get().orElseThrow(() -> new PackerException("Unable to run packer "));
      final String versionString = vbb.getExecution(VERSION).map(res -> {
        if (res.isError())
          throw new PackerException("Failed to get 'version' result",
              res.getException().orElse(new PackerException("internal version execution failure")));
        return res.getStdOut().iterator().next();
      }).orElseThrow(() -> new PackerException("no version execution"));
      if (!exists(factory.getManifestTargetPath()))
        throw new PackerException("Manifest target file does not exist " + factory.getManifestTargetPath());
      return vbb.getExecution(PACKER_EXECUTION).map(packVbb -> {
        if (packVbb.isError())
          throw new PackerException("Failed to execute packer",
              packVbb.getException().orElse(new PackerException("Unknown reason for failure")));
        final JSONObject manifestJSON = factory.getManifest();
        // FIXME Can't remember why Instant.now instead of packVbb.getStartTime()
        final PackerExecutionData e = new PackerExecutionData(versionString, Instant.now(), packVbb.getRunningtime(),
            packVbb.isTimedOut(), packer.toString(), factory.getPackerChecksum(), of(packVbb.getExecutionEnvironment()),
            packVbb.getStdOut(), of(packerFileAsJSON));

        return new PackerManifest(e, coords, manifestJSON, name, desc, c, factory);
      })

          .orElseThrow(() -> new PackerException("No " + PACKER_EXECUTION + " result was available"));

    }
  }

  private final PlexusContainer        container;
  private final GAV                    coords;
  private final String                 description;
  private final JSONObject             mergedManifest;
  private final String                 name;
  private final JSONObject             originalManifest;
  private final PackerExecutionData    packerExecutionData;
  private final List<ImageBuildResult> results;

  public PackerManifest(final JSONObject j, final PlexusContainer c) {
    originalManifest = requireNonNull(j.getJSONObject(ORIGINAL_MANIFEST));
    mergedManifest = requireNonNull(j.getJSONObject(MERGED_MANIFEST));
    packerExecutionData = new PackerExecutionData(j.getJSONObject(PACKER));
    coords = requireNonNull(new DefaultGAV(j.getJSONObject(GAV_KEY)));
    container = requireNonNull(c);
    description = requireNonNull(j.getString(DESCRIPTION));
    name = requireNonNull(j.getString(NAME));
    results = asJSONObjectStream(mergedManifest.getJSONArray(BUILDS)).map(aa -> reverseMapBuildResult(aa))
        .collect(toList());

  }

  public PackerManifest(final PackerExecutionData e, final GAV coords, final JSONObject j, final String name,
      final String desc, final PlexusContainer c, final PackerFactory factory) {
    originalManifest = requireNonNull(j);
    mergedManifest = mergeManifest(requireNonNull(factory).getHintMap().get(BUILDER), originalManifest);
    packerExecutionData = requireNonNull(e);
    this.coords = requireNonNull(coords);
    container = requireNonNull(c);
    description = requireNonNull(desc);
    this.name = requireNonNull(name);

    results = asJSONObjectStream(mergedManifest.getJSONArray(BUILDS)).map(aa -> reverseMapBuildResult(aa))
        .collect(toList());
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addJSONOutputEnabled(PACKER, packerExecutionData)

        .addJSONOutputEnabled(GAV_KEY, coords)

        .addJSONObject(ORIGINAL_MANIFEST, originalManifest)

        .addJSONObject(MERGED_MANIFEST, mergedManifest)

        .addString(NAME, name)

        .addString(DESCRIPTION, description)

        .asJSON();
  }

  public Optional<List<ImageBuildResult>> getBuilds() {
    return ofNullable(results);
  }

  public Optional<List<ImageBuildResult>> getBuildsForType(final IBAuthentication auth) {
    requireNonNull(auth);
    return getBuilds().map(b -> b.stream().filter(o -> o.matchesForAuth(auth)).collect(toList()));
  }

  public Optional<List<ImageBuildResult>> getBuildWithId(final UUID id) {
    final Optional<List<ImageBuildResult>> b = getBuilds();
    final Optional<List<ImageBuildResult>> res = b.map(arr -> {
      final List<ImageBuildResult> l = arr.stream().filter(x -> {
        final UUID runId = x.getRunUUID();
        return runId.equals(id);
      }).collect(toList());
      return l;
    });
    return res;
  }

  public Optional<GAV> getCoords() {
    return ofNullable(coords);
  }

  public Optional<List<ImageBuildResult>> getLastRun() {
    return getOptString(originalManifest, LAST_RUN_UUID).map(UUID::fromString).flatMap(this::getBuildWithId);
  }

  public JSONObject getMergedManifest() {
    return mergedManifest;
  }

  private JSONObject mergeManifest(final Map<String, IBRHintMap> l, final JSONObject j) {
    final JSONObject ret = new JSONObject(j.toString());
    final List<JSONObject> xy = asJSONObjectStream(j.getJSONArray(BUILDS)).map(build -> {
      final JSONObject r1 = new JSONObject(build.toString());
      final String name = r1.getString(NAME);
      final IBRHintMap s = ofNullable(l.get(name))
          .orElseThrow(() -> new PackerException("No value for " + name + " in builder map"));
      final String source = s.getHint().orElseThrow(() -> new PackerException("No source hint in " + s));
      r1.put(SOURCE, source);
      s.getAuthId().ifPresent(auth -> r1.put(ORIGINAL_AUTH_ID, auth));
      if (!s.getKlass().equals(ImageData.class))
        throw new PackerException("For some reason, " + s + " has a class type that isn't ImageData");
      return r1;
    }).collect(toList());
    return ret.put(BUILDS, new JSONArray(xy));
  }

  private ImageBuildResult reverseMapBuildResult(final JSONObject a) {
    final ImageData pb = et.withReturningTranslation(() -> container.lookup(ImageData.class, a.getString(SOURCE)));
    return pb.mapBuildResult(a);
  }
}
