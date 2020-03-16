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
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.automation.IBRExecutionData.EXECUTIONDATA;
import static org.infrastructurebuilder.automation.IBRManifest.MODEL_VERSION;
import static org.infrastructurebuilder.automation.IBRSpecificExecution.TYPE;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionBoundedRange;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.DefaultIBVersionBoundedRange;

abstract public class AbstractIBRExecutionDataReader<T extends IBRExecutionData>
    implements IBRSpecificExecutionReader<T> {

  private final String                hint;
  private final IBVersionBoundedRange boundedRange;

  public AbstractIBRExecutionDataReader(String hint, String lower, String upper) {
    this(hint, DefaultIBVersionBoundedRange.versionBoundedRangeFrom(lower, upper));
  }

  public AbstractIBRExecutionDataReader(String hint, IBVersionBoundedRange range) {
    this.hint = requireNonNull(hint);
    this.boundedRange = Objects.requireNonNull(range);
  }

  @Override
  public Optional<Supplier<T>> readExecutionData(Xpp3Dom ed) {
    return Optional.empty();
  }

  @Override
  public String getComponentHint() {
    return this.hint;
  }

  @Override
  public IBVersionBoundedRange getModelAPIVersionRange() {
    return this.boundedRange;
  }

  @Override
  public boolean respondsTo(Xpp3Dom ed) {
    // Subtypes should at least call this first
    return EXECUTIONDATA.equals(ed.getName())
        // Type should match hint
        && getComponentHint().equals(ed.getAttribute(TYPE))
        // Check model version
        && getModelAPIVersionRange().isSatisfiedBy(new DefaultIBVersion(ed.getAttribute(MODEL_VERSION)).apiVersion());

  }

  protected Optional<Xpp3Dom> getExecutionData(Xpp3Dom d) {
    return ofNullable(requireNonNull(d).getChild(EXECUTION_DATA));
  }
}
