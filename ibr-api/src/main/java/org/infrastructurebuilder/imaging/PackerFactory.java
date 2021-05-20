/*
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

import static org.infrastructurebuilder.automation.IBRAutomationException.et;
import static org.infrastructurebuilder.util.core.IBUtils.readFile;

import java.lang.System.Logger;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.codehaus.plexus.PlexusContainer;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.executor.ProcessExecutionFactory;
import org.json.JSONObject;

public interface PackerFactory extends Supplier<Path> {

  PackerFactory addBuilder(ImageData b);

  PackerFactory addPostProcessor(PackerPostProcessor p);

  PackerFactory addProvisioner(PackerProvisioner p);

  PackerFactory addUniqueBuilder(java.util.Comparator<ImageData> b, ImageData b1);

  PackerFactory addUniquePostProcessor(java.util.Comparator<PackerPostProcessor> b, PackerPostProcessor p);

  PackerFactory addUniqueProvisioner(java.util.Comparator<PackerProvisioner> b, PackerProvisioner p);

  PackerFactory addVariable(String name, String value);

  JSONObject asFilteredJSON(Properties props);

  Map<String, Path> collectAllForcedOutput();

  PlexusContainer getContainer();

  Map<String, Map<String,  IBRHintMap>> getHintMap();

  Logger getLog();

  Path getManifestPath();

  Path getManifestTargetPath();

  default JSONObject getManifest() {
    return et.withReturningTranslation(() -> new JSONObject(readFile(getManifestTargetPath())));
  }

  Path getMetaRoot();

  Checksum getPackerChecksum();

  Path getPackerExecutable();

  JSONObject getBuilderOutputData();

  List<IBRInternalDependency> getRequirements();

  Path getRoot();

  ProcessExecutionFactory getProcessExecutionFactory(String id);


}