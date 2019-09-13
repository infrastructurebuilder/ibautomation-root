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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.codehaus.plexus.PlexusContainer;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.json.JSONObject;
import org.slf4j.Logger;

public interface PackerFactory<T> extends Supplier<Optional<Path>> {

  PackerFactory<T> addBuilder(ImageData<T> b);

  PackerFactory<T> addPostProcessor(PackerPostProcessor p);

  PackerFactory<T> addProvisioner(PackerProvisioner<T> p);

  PackerFactory<T> addUniqueBuilder(java.util.Comparator<ImageData<T>> b, ImageData<T> b1);

  PackerFactory<T> addUniquePostProcessor(java.util.Comparator<PackerPostProcessor> b, PackerPostProcessor p);

  PackerFactory<T> addUniqueProvisioner(java.util.Comparator<PackerProvisioner<T>> b, PackerProvisioner<T> p);

  PackerFactory<T> addVariable(String name, String value);

  JSONObject asFilteredJSON(Properties props);

  Map<String, Path> collectAllForcedOutput();

  PlexusContainer getContainer();

  Optional<Map<String, Map<String, PackerHintMapDAO>>> getHintMap();

  Logger getLog();

  Path getManifestPath();

  Path getManifestTargetPath();

  Optional<Path> getMetaRoot();

  Optional<Checksum> getPackerChecksum();

  Path getPackerExecutable();

  T getBuilderOutputData();

  List<IBRInternalDependency> getRequirements();

  Path getRoot();

}