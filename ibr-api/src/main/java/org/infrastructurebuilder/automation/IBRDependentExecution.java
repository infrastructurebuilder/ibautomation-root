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
package org.infrastructurebuilder.automation;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;
import org.infrastructurebuilder.util.artifacts.GAV;

public interface IBRDependentExecution extends ChecksumEnabled {
  GAV getGav();

  default Optional<Supplier<? extends IBRTypedExecution>> getTypedExecutionData() {
    return java.util.Optional.ofNullable(getParent()).flatMap(IBRManifest::getMapper)
        // If we have a mapper
        .flatMap(mapper -> mapper.readIBRData(getExecutionData(), this));
  }

  Optional<Xpp3Dom> getExecutionData();

  List<String> getLogLines();

  IBRManifest getParent();

  @Override
  default Checksum asChecksum() {
    return ChecksumBuilder.newInstance()

        .addChecksumEnabled(getGav())

        .addChecksumEnabled(getTypedExecutionData().map(f -> f.get()).orElse(null))

        .asChecksum();
  }

}
