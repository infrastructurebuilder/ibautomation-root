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
package org.infrastructurebuilder.automation;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.ChecksumEnabled;
import org.infrastructurebuilder.util.core.IBUtils;

public interface IBRManifest extends ChecksumEnabled, Comparable<IBRManifest> {
  public final static Xpp3DomBuilder domBuilder     = new Xpp3DomBuilder();
  public static final String         MODEL_VERSION  = "modelVersion";
  public static final String         MODEL_ENCODING = "modelEncoding";

  public static final DateTimeFormatter dtf = ISO_DATE_TIME.withZone(UTC);

  UUID getId();

  Optional<String> getName();

  Optional<String> getDescription();

  Instant getStart();

  /**
   * Returns the end time, or empty() if not completed.
   *
   * Note that the end value is still required for persistence.
   *
   * @return
   */
  Optional<Instant> getEnd();

  default Optional<Duration> getDuration() {
    return getEnd().map(end -> Duration.between(getStart(), end));
  }

  String getBuilderId();

  Properties getProperties();

  Properties getEnvironment();

  List<IBRDependentExecution> getDependentExecutions();

  Optional<IBRExecutionDataReaderMapper> getMapper();

  @Override
  default int compareTo(IBRManifest o) {
    int retVal = getStart().compareTo(o.getStart());
    if (retVal == 0) {
      retVal = IBUtils.nullSafeInstantComparator.compare(getEnd().orElse(null), o.getEnd().orElse(null));
      if (retVal == 0) {
        retVal = getId().compareTo(o.getId());
      }
    }
    return retVal;
  }

  @Override
  default Checksum asChecksum() {
    return ChecksumBuilder.newInstance().addString(getId().toString()).addString(getName()).addString(getDescription())
        .addInstant(getStart()).addInstant(getEnd())
        .addListChecksumEnabled(getDependentExecutions().stream().collect(toList())).asChecksum();
  }

}
