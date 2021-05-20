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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.lang.System.Logger;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedSet;


public class IBRDataObject<T> {
  public final static Logger log = System.getLogger(IBRDataObject.class.getName());
  private final IBRBuilderConfigElement builder;
  private final String name;
  private final SortedSet<IBRValidationOutput> output;
  private final Path targetPath;

  public IBRDataObject(final IBRType type, final Path targetPath,
      final IBRBuilderConfigElement builderJson) {
    name = requireNonNull(type).getName();

    output = type.collectValidatedOutput();
    this.targetPath = targetPath;
    builder = requireNonNull(builderJson);

    final List<IBRValidationOutput> l = output.stream().filter(o -> !o.isValid()).collect(toList());
    l.forEach(ll -> log.log(Logger.Level.ERROR,"Error!  Path is " + ll.getPath() + "\nException is " + ll.getException().get()));
    if (l.size() > 0)
      throw new IBArchiveException("Failure of " + l.size() + " entries");
  }

  public IBRBuilderConfigElement getBuilder() {
    return builder;
  }

  public int getCount() {
    return output.size();
  }

  public String getName() {
    return name;
  }

  public SortedSet<IBRValidationOutput> getOutput() {
    return output;
  }

  public Path getTargetPath() {
    return targetPath;
  }

}
