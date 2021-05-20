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
package org.infrastructurebuilder.configuration.management;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.imaging.ImageData;
import org.json.JSONObject;

public interface IBRType {
  AutomationUtils getAutomationUtils();

  SortedSet<IBRValidationOutput> collectValidatedOutput();

  default Path getArchiveSubPath() {
    return Paths.get(getName());
  }

  Optional<Map<String, Object>> getConfig();

  String getId();

  String getName();

  Set<IBRValidator> getRelevantValidators();

  void setConfigSupplier(IBConfigSupplier acs);

  JSONObject transformToProvisionerEntry(String typeName, Path root, Path targetFile, Optional<IBArchive> archive,
      List<ImageData> builders);
}
