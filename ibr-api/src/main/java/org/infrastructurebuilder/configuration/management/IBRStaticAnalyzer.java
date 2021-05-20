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
package org.infrastructurebuilder.configuration.management;

import java.lang.System.Logger;
import java.util.Arrays;
import java.util.List;


public interface IBRStaticAnalyzer {
  default List<String> getDisallowedStringList() {
    return Arrays.asList("executionId");
  }

  System.Logger getLog();

  default boolean isValid(final String toValidate) {
    final List<String> wrappers = Arrays.asList("${%s}", "@%s@");
    for (final String disallowed : getDisallowedStringList()) {
      for (final String wrapper : wrappers)
        if (toValidate.contains(String.format(wrapper, disallowed))) {
          getLog().log(Logger.Level.ERROR,String.format("DISALLOWED %s  / %s", disallowed, wrapper));
          return false;
        }
    }
    return true;
  }
}
