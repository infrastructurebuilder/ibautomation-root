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

import static java.util.Arrays.asList;
import static java.util.Optional.of;

import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.PackerProvisioner;

@Typed(PackerProvisioner.class)
@Named(PackerYumUpdateProvisioner.YUM_UPDATE_PROVISIONER)
@Description("Updates packages on a YUM-style box")
public class PackerYumUpdateProvisioner extends PackerShellProvisioner {
  public static final String YUM_UPDATE_PROVISIONER = "yum-update-provisioner";

  public PackerYumUpdateProvisioner() {
    setInlines(asList("sleep 30", "sudo yum -y update"));
  }

  @Override
  public Optional<String> getLookupHint() {
    return of(YUM_UPDATE_PROVISIONER);
  }
}
