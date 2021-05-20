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
package org.infrastructurebuilder.imaging.shell;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.SHELL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.json.JSONObject;

@Named(PackerShellProvisioner.SHELL_PROVISIONER)
@Typed(PackerProvisioner.class)
public class PackerShellProvisioner extends PackerShellPostProcessor implements PackerProvisioner {
  public static final String SHELL_PROVISIONER = "shell";

  static final List<String> namedTypes = new ArrayList<String>() {
    /**
     *
     */
    private static final long serialVersionUID = -3075709617196193529L;

    /**
     *
     */

    {
      add(SHELL);
    }
  };

  private List<ImageData> builders = Collections.emptyList();

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(SHELL_PROVISIONER);
  }

  @Override
  public List<String> getNamedTypes() {
    return namedTypes;
  }

  @Override
  public Optional<JSONObject> getOverrides() {

    return Optional.empty();
  }

  @Override
  public String getPackerType() {
    return SHELL_PROVISIONER;
  }

  @Override
  public void setBuilders(final List<ImageData> builders) {
    this.builders = Objects.requireNonNull(builders);
  }

  @Override
  public PackerProvisioner updateWithOverrides(final List<ImageData> builders) {

    return this;
  }

  protected List<ImageData> getBuilders() {
    return builders;
  }

}
