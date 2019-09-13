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

import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.DESCRIPTION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.GAV_KEY;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.LAST_RUN_UUID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.MERGED_MANIFEST;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_AUTH_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_MANIFEST;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE;
import static org.infrastructurebuilder.util.IBUtils.asJSONObjectStream;
import static org.infrastructurebuilder.util.IBUtils.getOptString;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.codehaus.plexus.PlexusContainer;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerHintMapDAO;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONArray;
import org.json.JSONObject;

public class PackerManifest implements JSONOutputEnabled {

  private final PlexusContainer container;
  private final GAV coords;
  private final String description;
  private final JSONObject mergedManifest;
  private final String name;
  private final JSONObject originalManifest;
  private final PackerExecutionData packerExecutionData;
  private final List<ImageBuildResult> results;

  public PackerManifest(final JSONObject j, final PlexusContainer c) {
    originalManifest = Objects.requireNonNull(j.getJSONObject(ORIGINAL_MANIFEST));
    mergedManifest = Objects.requireNonNull(j.getJSONObject(MERGED_MANIFEST));
    packerExecutionData = new PackerExecutionData(j.getJSONObject(PACKER));
    coords = Objects.requireNonNull(new DefaultGAV(j.getJSONObject(GAV_KEY)));
    container = Objects.requireNonNull(c);
    description = Objects.requireNonNull(j.getString(DESCRIPTION));
    name = Objects.requireNonNull(j.getString(NAME));
    results = asJSONObjectStream(mergedManifest.getJSONArray(BUILDS)).map(aa -> reverseMapBuildResult(aa))
        .collect(toList());

  }

  public PackerManifest(final PackerExecutionData e, final GAV coords, final JSONObject j, final String name,
      final String desc, final PlexusContainer c, final PackerFactory<JSONObject> factory) {
    originalManifest = Objects.requireNonNull(j);
    mergedManifest = mergeManifest(factory, originalManifest);
    packerExecutionData = Objects.requireNonNull(e);
    this.coords = Objects.requireNonNull(coords);
    container = Objects.requireNonNull(c);
    description = Objects.requireNonNull(desc);
    this.name = Objects.requireNonNull(name);

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
    return Optional.ofNullable(results);
  }

  public Optional<List<ImageBuildResult>> getBuildsForType(final IBAuthentication auth) {
    Objects.requireNonNull(auth);
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
    return Optional.ofNullable(coords);
  }

  public Optional<List<ImageBuildResult>> getLastRun() {
    return getOptString(originalManifest, LAST_RUN_UUID).map(UUID::fromString).flatMap(this::getBuildWithId);
  }

  public JSONObject getMergedManifest() {
    return mergedManifest;
  }

  private JSONObject mergeManifest(final PackerFactory<JSONObject> pf, final JSONObject j) {
    final Map<String, Map<String, PackerHintMapDAO>> hm = Objects.requireNonNull(pf).getHintMap()
        .orElseThrow(() -> new PackerException("No hint map in " + pf));
    final JSONObject ret = new JSONObject(j.toString());
    final Map<String, PackerHintMapDAO> l = hm.get(BUILDER);
    final List<JSONObject> xy = asJSONObjectStream(j.getJSONArray(BUILDS)).map(build -> {
      final JSONObject r1 = new JSONObject(build.toString());
      final String name = r1.getString(NAME);
      final PackerHintMapDAO s = Optional.ofNullable(l.get(name))
          .orElseThrow(() -> new PackerException("No value for " + name + " in builder map"));
      final String source = s.getHint().orElseThrow(() -> new PackerException("No source hint in " + s));
      r1.put(SOURCE, source);
      final Optional<String> sAuth = s.getAuthId();
      sAuth.ifPresent(auth -> r1.put(ORIGINAL_AUTH_ID, auth));
      if (!s.getKlass().equals(ImageData.class))
        throw new PackerException("For some reason, " + s + " has a class type that isn't ImageData");
      return r1;
    }).collect(toList());
    return ret.put(BUILDS, new JSONArray(xy));
  }

  private ImageBuildResult reverseMapBuildResult(final JSONObject a) {

    @SuppressWarnings("unchecked")
    final ImageData<JSONObject> pb = PackerException.et
        .withReturningTranslation(() -> container.lookup(ImageData.class, a.getString(SOURCE)));
    return pb.mapBuildResult(a);
  }

}
