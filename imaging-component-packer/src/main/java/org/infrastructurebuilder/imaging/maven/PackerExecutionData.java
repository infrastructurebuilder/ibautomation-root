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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.CHECKSUM;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ENVIRONMENT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.EXECUTABLE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.EXECUTION_DURATION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.LOG_LINES;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINALSOURCE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_EXECUTABLE_CHECKSUM;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.START_TIME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TIMED_OUT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.VERSION;
import static org.infrastructurebuilder.util.IBUtils.asStringStream;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.JSONAndChecksumEnabled;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackerExecutionData implements JSONAndChecksumEnabled {
  private final static Logger log = LoggerFactory.getLogger(PackerExecutionData.class);

  private final Checksum                      checksum;
  private final Duration                      duration;
  private final List<String>                  mrLogs;
  private final Optional<JSONObject>          originalSource;
  private final Path                          packerExecutable;
  private final Checksum                      packerExecutableChecksum;
  private final Optional<Map<String, String>> packerExecutionEnvironment;
  private final Optional<String>              packerVersion;
  private final Instant                       startTime;
  private final boolean                       timedOut;

  public PackerExecutionData(final JSONObject packerJson) {
    final JSONObject pJson = requireNonNull(packerJson);
    startTime = Instant.parse(pJson.getString(START_TIME));
    timedOut = packerJson.getBoolean(TIMED_OUT);
    duration = Duration.parse(pJson.getString(EXECUTION_DURATION));
    packerExecutable = Paths.get(pJson.getString(EXECUTABLE)).toAbsolutePath();
    packerVersion = ofNullable(pJson.optString(VERSION, null));
    packerExecutableChecksum = new Checksum(pJson.getString(PACKER_EXECUTABLE_CHECKSUM));
    packerExecutionEnvironment = ofNullable(pJson.optJSONObject(ENVIRONMENT))
        .map(o -> o.toMap().entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue().toString())));
    mrLogs = asStringStream(pJson.getJSONArray(LOG_LINES)).collect(toList());
    originalSource = ofNullable(pJson.optJSONObject(ORIGINALSOURCE));
    checksum = _getChecksum();
    final Optional<Checksum> c = ofNullable(pJson.optString(CHECKSUM, null)).map(Checksum::new);
    c.ifPresent(possible -> {
      if (!possible.equals(checksum)) {
        log.warn("Checksum failure" + checksum + " vs " + possible);
      }
    });

  }

  public PackerExecutionData(final String packerVersion, final Instant startTime, final Duration runTime,
      final boolean timedOut, final String packerExecutable, final Checksum packerExecutableChecksum,
      final Optional<Map<String, String>> packerExecutionEnvironment, final List<String> mrLogs,
      final Optional<JSONObject> originalSource) {
    this.startTime = requireNonNull(startTime);
    this.timedOut = timedOut;
    this.duration = requireNonNull(runTime);
    this.packerExecutable = Paths.get(requireNonNull(packerExecutable)).toAbsolutePath();
    this.packerVersion = Optional.of(packerVersion);
    this.packerExecutableChecksum = requireNonNull(packerExecutableChecksum);
    this.packerExecutionEnvironment = requireNonNull(packerExecutionEnvironment);
    this.mrLogs = requireNonNull(mrLogs);
    this.originalSource = requireNonNull(originalSource);
    this.checksum = _getChecksum();
  }

  @Override
  public final Checksum asChecksum() {
    return checksum;
  }

  @Override
  public JSONObject asJSON() {

    final JSONObject j = JSONBuilder.newInstance()

        .addString(EXECUTABLE, packerExecutable.toString())

        .addChecksum(PACKER_EXECUTABLE_CHECKSUM, packerExecutableChecksum)

        .addInstant(START_TIME, startTime)

        .addDuration(EXECUTION_DURATION, duration)

        .addBoolean(TIMED_OUT, timedOut)

        .addString(VERSION, packerVersion)

        .addMapStringString(ENVIRONMENT, packerExecutionEnvironment)

        .addJSONArray(LOG_LINES, new JSONArray(mrLogs))

        .addChecksum(CHECKSUM, checksum).asJSON();

    originalSource.ifPresent(e -> j.put(ORIGINALSOURCE, e));

    return j;
  }

  public Duration getDuration() {
    return duration;
  }

  public List<String> getMachineReadableLogs() {
    return mrLogs;
  }

  public Optional<JSONObject> getOriginalSource() {
    return originalSource;
  }

  public String getPackerExecutable() {
    return packerExecutable.toString();
  }

  public Checksum getPackerExecutableChecksum() {
    return packerExecutableChecksum;
  }

  public Optional<Map<String, String>> getPackerExecutionEnvironment() {
    return packerExecutionEnvironment;
  }

  public Optional<String> getPackerVersion() {
    return packerVersion;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Instant getStopTime() {
    return getStartTime().plus(getDuration());
  }

  public boolean isTimedOut() {
    return timedOut;
  }

  private Checksum _getChecksum() {
    return ChecksumBuilder.newInstance()

        .addPath(packerExecutable)

        .addChecksum(packerExecutableChecksum)

        .addInstant(startTime)

        .addDuration(duration)

        .addString(packerVersion)

        .addMapStringString(packerExecutionEnvironment)

        .addListString(mrLogs)

        .addBoolean(timedOut)

        .asChecksum();
  }
}
