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
package org.infrastructurebuilder.maven.ibr;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.DummyIBRType;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.IBRValidationOutput;

@Named("fake")
@Typed(IBRType.class)
public class FakeDummyIBRType extends DummyIBRType {
  public final static String FAKE_TYPE_FILE = "fakeTypeFile";

  @Override
  public SortedSet<IBRValidationOutput> collectValidatedOutput() {

    final SortedSet<IBRValidationOutput> result = new TreeSet<>();
    final Path target = Paths.get(Optional.ofNullable(System.getProperty("target_directory")).orElse("./target"))
        .toAbsolutePath();
    result.add(new IBRValidationOutput(Paths.get(target.toString(), FAKE_TYPE_FILE), "fake", Optional.empty()));
    return result;
  }
}
