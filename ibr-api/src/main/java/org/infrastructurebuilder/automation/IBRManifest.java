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

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;

public interface IBRManifest extends ChecksumEnabled, Comparable<IBRManifest> {
  public final static Xpp3DomBuilder domBuilder    = new Xpp3DomBuilder();
  public static final String         MODEL_VERSION = "modelVersion";

  UUID getId();

  Optional<String> getName();

  Optional<String> getDescription();

  Date getStart();

  Date getEnd();

  String getBuilderId();

  Properties getProperties();

  Properties getEnvironment();

  List<IBRDependentExecution> getDependentExecutions();

  Optional<IBRExecutionDataReaderMapper> getMapper();

  @Override
  default int compareTo(IBRManifest o) {
    int retVal = getStart().compareTo(o.getStart());
    if (retVal == 0) {
      retVal = getEnd().compareTo(o.getEnd());
      if (retVal == 0) {
        retVal = getId().compareTo(o.getId());
      }
    }
    return retVal;
  }

  @Override
  default Checksum asChecksum() {
    return ChecksumBuilder.newInstance().addString(getId().toString()).addString(getName()).addString(getDescription())
        .addDate(getStart()).addDate(getEnd())
        .addListChecksumEnabled(getDependentExecutions().stream().collect(toList())).asChecksum();
  }

  default Duration getDuration() {
    return Duration.between(getStart().toInstant(), getEnd().toInstant());
  }

}
