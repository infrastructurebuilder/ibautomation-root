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
package org.infrastructurebuilder.configuration.management.shell;

import static org.infrastructurebuilder.configuration.management.IBRConstants.SHELL_FILE;
import static org.infrastructurebuilder.configuration.management.IBRConstants.TYPE;
import static org.infrastructurebuilder.configuration.management.shell.ShellConstants.SHELL;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.configuration.management.AbstractIBRType;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.configuration.management.IBRRootPathSupplier;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.IBRValidator;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

@Named(SHELL)
@Typed(IBRType.class)
public class ShellIBRType extends AbstractIBRType<JSONObject> {

  @Inject
  public ShellIBRType(final IBRRootPathSupplier rps, final List<IBRValidator> validators) {
    super(rps, validators);
    setName(SHELL);
  }

  @Override
  public JSONObject transformToProvisionerEntry(final String typeName, final Path root, final Path targetFile,
      final Optional<IBArchive> archive, final List<ImageData<JSONObject>> builders) {

    final JSONObject j = JSONBuilder.newInstance(Optional.ofNullable(getRoot())).addString(TYPE, getName())
        .addPath(SHELL_FILE, Objects.requireNonNull(targetFile)).asJSON();
    return j;
  }

}
