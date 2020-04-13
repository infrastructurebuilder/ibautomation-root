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

import java.util.Optional;
import java.util.function.Supplier;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionBoundedRange;
import org.infrastructurebuilder.util.artifacts.Weighted;

public interface IBRTypedExecutionReader<T extends IBRTypedExecution> extends Weighted {

  public static final String EXECUTION_DATA = "executionData";

  /**
   * Return the underlying component hint for this reader
   *
   * @return
   */
  String getComponentHint();

  String getWorkingSpecificDataType();

  /**
   * Can this reader work with this data?
   *
   * Note that this is not the same as will it read the data, only that the reader
   * considers it possible.
   *
   * This might potentially get called a lot, so it needs to be reasonably fast.
   *
   * @param ed Supplied data
   * @return true if this reader is capable of reading the specified data.
   */
  boolean respondsTo(Xpp3Dom ed);

  /**
   * Return some object as part of the encapsulated data set
   *
   * @param ed
   * @return data object, if possible
   */
  Optional<Supplier<T>> readTypedExecution(Xpp3Dom ed, IBRDependentExecution parent);

  IBVersionBoundedRange getModelAPIVersionRange();

}
