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
package org.infrastructurebuilder.configuration.management;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.util.IBUtils.asJSONObjectStream;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.json.JSONArray;
import org.json.JSONObject;

public class IBArchive implements JSONOutputEnabled {
  public static final String IBR           = "ibr";
  public static final String DATA          = "data";
  public static final String ID            = "id";
  public static final String RELATIVE_ROOT = "root";
  private final String       id;

  private final boolean            locked;
  private final Path               root;
  private final List<PathMapEntry> theList;

  public IBArchive(final JSONObject j, final Path root) {
    locked = true;
    id = requireNonNull(j).getString(ID);
    this.root = requireNonNull(root);
    theList = unmodifiableList(asJSONObjectStream(j.getJSONArray(DATA)).map(j2 -> {
      final String key = j2.keys().next();
      return new PathMapEntry(key, Paths.get(j2.getString(key)));
    }).collect(toList()));
  }

  public IBArchive(final Path root) {
    id = UUID.randomUUID().toString();
    theList = new ArrayList<>();
    this.root = requireNonNull(root);
    locked = false;
  }

  public IBArchive addMetadata(final String builder, Path out) {
    if (locked)
      throw new IBArchiveException("No additions to locked archive");
    if (out.isAbsolute()) {
      out = root.relativize(out);
    }
    theList.add(new PathMapEntry(builder, out));
    return this;
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(ID, getId())

        .addJSONArray(DATA, asJSONArray())

        .asJSON();
  }

  public List<String> getPathKeys() {
    return theList.stream().map(PathMapEntry::getKey).distinct().collect(toList());
  }

  public List<PathMapEntry> getPathList() {
    return theList.stream().collect(toList());
  }

  private JSONArray asJSONArray() {
    return new JSONArray(theList.stream().map(JSONOutputEnabled::asJSON).collect(toList()));
  }

  private String getId() {
    return id;
  }

}
