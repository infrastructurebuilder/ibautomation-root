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

import static java.util.Optional.empty;
import static org.infrastructurebuilder.util.core.Checksum.fromUTF8StringBytes;
import static org.infrastructurebuilder.util.core.GAV.GAV_VERSION;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.Xpp3OutputEnabled;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.ChecksumEnabled;

/**
 * A mapped representation of the XML stored in the "executionData" of a real
 * instance of a {@link IBRManifest} from the call
 * {@code IBRManifest#getDependentExecutions()#getSpecificExecutionData()}
 *
 * By contract the XML root tag for {@link IBRTypedExecution} must be
 * "executionData"
 *
 * By contract, originalSource, checksum, start, end, executable, and
 * specificData must all be present as children of executionData
 *
 * The formats for all of these except specificData are set.
 *
 * The specificData tag will be utilized by a versioned specific reader. See
 *
 * @author mykel.alvis
 *
 */
public interface IBRTypedExecution extends ChecksumEnabled, Xpp3OutputEnabled {
  public static final String EXECUTIONDATA   = "executionData";
  public static final String ORIGINAL_SOURCE = "originalSource";
  public static final String CHECKSUM        = "checksum";
  public static final String END             = "end";
  public static final String START           = "start";
  public static final String EXECUTABLE      = "executable";
  public static final String DATA            = "data";
  public static final String SPECIFICDATA    = "specificData";
  public static final String ENVIRONMENT     = "environment";
  public static final String VERSION         = GAV_VERSION;


  IBRTypedExecution copy();

  Instant getStart();

  Instant getEnd();

  String getExecutable();

  Checksum getExecutionChecksum();

  default Duration getDuration() {
    return Duration.between(getStart(), getEnd());
  }

  String getSpecificExecutionSource();

  default Optional<IBRImageMap> getImageMap() {
    return empty();
  }

  IBRSpecificExecution getSpecificExecutionData();

  @Override
  default Checksum asChecksum() {
    return ChecksumBuilder.newInstance()

        .addInstant(getStart())

        .addInstant(getEnd())

        .addString(getExecutable())

        .addChecksum(getExecutionChecksum())

        .addChecksum(fromUTF8StringBytes(getSpecificExecutionSource()))

        .addChecksumEnabled(getSpecificExecutionData())

        .asChecksum();
  }

  IBRDependentExecution getParent();

}
