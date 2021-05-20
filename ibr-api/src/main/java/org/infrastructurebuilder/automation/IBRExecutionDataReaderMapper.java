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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.util.constants.IBConstants.DEFAULT;
import static org.infrastructurebuilder.util.core.Weighted.weighted;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.xml.Xpp3Dom;

@Named(DEFAULT)
@Singleton
public final class IBRExecutionDataReaderMapper {

  public static Comparator<? super IBRTypedExecutionReader<?>> weightedHint = new Comparator<IBRTypedExecutionReader<?>>() {
    @Override
    public int compare(IBRTypedExecutionReader<?> o1, IBRTypedExecutionReader<?> o2) {
      int retVal = weighted.compare(o1, o2);
      if (retVal == 0)
        retVal = o1.getComponentHint().compareTo(o2.getComponentHint());
      return retVal;
    }
  };

  private final List<IBRTypedExecutionReader<?>> readers;

  @Inject
  public IBRExecutionDataReaderMapper(Map<String, IBRTypedExecutionReader<?>> readers) {
    this.readers = requireNonNull(readers).values().stream().sorted(weightedHint).collect(toList());
  }

  public Optional<Supplier<? extends IBRTypedExecution>> readIBRData(Object d, IBRDependentExecution de) {
    requireNonNull(de);
    Xpp3Dom e;
    try {
      e = (Xpp3Dom) requireNonNull(d);
    } catch (Throwable t) {
      return empty();
    }
    Optional<IBRTypedExecutionReader<?>> q = this.readers.stream().filter(f -> f.respondsTo(e)).findFirst();
    // Type juggling kinda sucks
    return ofNullable(!q.isPresent() ? null : q.get().readTypedExecution(e, de).orElse(null));
  }

}
