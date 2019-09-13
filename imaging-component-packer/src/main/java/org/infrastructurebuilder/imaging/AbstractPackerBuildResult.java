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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.ARTIFACT_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER_TYPE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_TIME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.FILES;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_AUTH_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_RUN_UUID;
import static org.infrastructurebuilder.util.IBUtils.getOptionalJSONArray;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infrastructurebuilder.imaging.maven.LocalFileResult;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractPackerBuildResult implements ImageBuildResult {

  private final Optional<String> artifactId;
  private final String builderType;
  private final Instant buildTime;
  private final Optional<List<Object>> files;
  private final Optional<JSONArray> filesList;
  private final JSONObject j;
  private final String name;
  private final Optional<String> oaid;
  private final UUID uuid;

  public AbstractPackerBuildResult(final JSONObject j) {
    this.j = Objects.requireNonNull(j);
    filesList = getOptionalJSONArray(getJSON(), FILES);
    artifactId = Optional.ofNullable(getJSON().optString(ARTIFACT_ID));
    builderType = getJSON().getString(BUILDER_TYPE);
    buildTime = new Date(getJSON().getLong(BUILD_TIME)).toInstant();
    files = _getFilesList().map(a -> {
      return IBUtils.asJSONObjectStream(a).map(LocalFileResult::new).collect(Collectors.toList());
    });
    name = getJSON().getString(NAME);
    uuid = UUID.fromString(getJSON().getString(PACKER_RUN_UUID));
    oaid = IBUtils.getOptString(j, ORIGINAL_AUTH_ID);
  }

  @Override
  public Optional<String> getArtifactInfo() {
    return artifactId;
  }

  @Override
  public String getBuilderType() {
    return builderType;
  }

  @Override
  public Instant getBuildTime() {
    return buildTime;
  }

  @Override
  public Optional<List<Object>> getFiles() {
    return files;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Optional<String> getOriginalAuthId() {
    return oaid;
  }

  @Override
  public UUID getRunUUID() {
    return uuid;
  }

  @Override
  public boolean matchesForAuth(final IBAuthentication auth) {
    if (Objects.requireNonNull(auth).getType().equals(getBuilderType())) {
      final Optional<String> aoid = getOriginalAuthId();
      if (aoid.isPresent())
        return aoid.get().equals(auth.getId());
      else
        return true;
    }

    return false;
  }

  protected Optional<JSONArray> _getFilesList() {
    return filesList;
  }

  protected JSONObject getJSON() {
    return j;
  }

}
