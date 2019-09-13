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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.auth.IBAuthentication;

public interface ImageBuildResult {

  Optional<String> getArtifactInfo();

  String getBuilderType();

  Instant getBuildTime();

  Optional<List<Object>> getFiles();

  String getName();

  Optional<String> getOriginalAuthId();

  UUID getRunUUID();

  boolean matchesForAuth(IBAuthentication auth);
}
