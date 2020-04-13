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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.automation.IBRManifest.MODEL_VERSION;
import static org.infrastructurebuilder.automation.IBRSpecificExecution.TYPE;
import static org.infrastructurebuilder.automation.IBRTypedExecution.EXECUTIONDATA;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionBoundedRange;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.DefaultIBVersionBoundedRange;

/**
 * The {@code AbstractIBRExecutionDataReader} is the base for a reader for an {@link IBRTypedExecution}
 *
 * The {@code AbstractIBRExecutionDataReader} is point at which the various models start to disassociate from each other,
 * while string retaining a line of connectivity.
 *
 * @author mykel.alvis
 *
 * @param <T>
 */
abstract public class AbstractIBRExecutionDataReader<T extends IBRTypedExecution>
    implements IBRTypedExecutionReader<T> {

  private final String                hint;
  private final IBVersionBoundedRange boundedRange;
  private final String workingType;

  public AbstractIBRExecutionDataReader(String workingType, String hint, String lower, String upper) {
    this(workingType, hint, DefaultIBVersionBoundedRange.versionBoundedRangeFrom(lower, upper));
  }

  public AbstractIBRExecutionDataReader(String workingType, String hint, IBVersionBoundedRange range) {
    this.workingType = requireNonNull(workingType);
    this.hint = requireNonNull(hint);
    this.boundedRange = Objects.requireNonNull(range).apiRange();
  }

  @Override
  public Optional<Supplier<T>> readTypedExecution(Xpp3Dom ed, IBRDependentExecution parent) {
    return Optional.empty();
  }

  @Override
  public String getComponentHint() {
    return this.hint;
  }

  @Override
  public String getWorkingSpecificDataType() {
    return this.workingType;
  }

  @Override
  public IBVersionBoundedRange getModelAPIVersionRange() {
    return this.boundedRange;
  }

  @Override
  public boolean respondsTo(Xpp3Dom s) {
    return Optional.ofNullable(s).map(ed -> {
      return Optional.ofNullable(ed.getChild(IBRTypedExecution.SPECIFICDATA)).map(spd -> {
        // Subtypes should at least call this first
        return EXECUTIONDATA.equals(ed.getName())
            // Type should match hint
            && getWorkingSpecificDataType().equals(spd.getAttribute(TYPE))
        // Check model version
            && getModelAPIVersionRange()
                .isSatisfiedBy(new DefaultIBVersion(spd.getAttribute(MODEL_VERSION)).apiVersion());
      }).orElse(false);
    }).orElse(false);

  }

}
