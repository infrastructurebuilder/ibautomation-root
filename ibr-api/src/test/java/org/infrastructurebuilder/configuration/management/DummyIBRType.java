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

import static org.infrastructurebuilder.ibr.IBRConstants.ANSIBLE;
import static org.infrastructurebuilder.ibr.IBRConstants.PLAYBOOK_FILE;
import static org.infrastructurebuilder.ibr.IBRConstants.TYPE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.imaging.ImageData;
import org.json.JSONObject;

public class DummyIBRType extends AbstractIBRType {
  private static Map<String, String> params = new HashMap<>();
  private final static AutomationUtilsTesting rps = new AutomationUtilsTesting();
  static {
    params.put("file", "required");
  }

  public static AutomationUtilsTesting getRps() {
    return rps;
  }

  public DummyIBRType() {
    this(Collections.emptySortedSet(), Collections.emptySet());
  }

  public DummyIBRType(final SortedSet<IBRValidationOutput> oSet, final Set<IBRValidator> vset) {
    super(rps, new ArrayList<>(vset));
    setName("fake");
  }

  @Override
  public JSONObject transformToProvisionerEntry(final String typeName, final Path root, final Path targetFile,
      final Optional<IBArchive> archive, final List<ImageData> builders) {
    return new JSONObject().put(TYPE, ANSIBLE).put(PLAYBOOK_FILE, "./path/to/file");
  }

}
