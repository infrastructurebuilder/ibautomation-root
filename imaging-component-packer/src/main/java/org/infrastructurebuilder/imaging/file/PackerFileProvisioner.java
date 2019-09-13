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
package org.infrastructurebuilder.imaging.file;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.DESTINATION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.DIRECTION;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.GENERATED;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.AbstractPackerProvisioner;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

@Named(PackerFileBuilder.FILETYPE)
@Typed(PackerProvisioner.class)
@Description("Send a file to remote machine")
public class PackerFileProvisioner extends AbstractPackerProvisioner<JSONObject> {
  private String destination;
  private PackerFileProvisionerDirection direction = PackerFileProvisionerDirection.upload;
  private boolean generated = false;
  private String source;

  @Inject
  public PackerFileProvisioner() {
    super();
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(TYPE, getPackerType())

        .addString(SOURCE, getSource())

        .addString(DESTINATION, getDestination())

        .addString(DIRECTION, getDirection().map(PackerFileProvisionerDirection::name))

        .addBoolean(GENERATED, isGenerated())

        .asJSON();
  }

  public Optional<String> getDestination() {
    return Optional.ofNullable(destination);
  }

  public Optional<PackerFileProvisionerDirection> getDirection() {
    return Optional.ofNullable(direction == PackerFileProvisionerDirection.download ? direction : null);
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(PackerFileBuilder.FILETYPE);
  }

  @Override
  public List<String> getNamedTypes() {
    return PackerFileBuilder.namedTypes;
  }

  @Override
  public String getPackerType() {
    return PackerFileBuilder.FILETYPE;
  }

  public Optional<String> getSource() {
    return Optional.ofNullable(source);
  }

  public Optional<Boolean> isGenerated() {
    return Optional.ofNullable(generated ? Boolean.TRUE : null);
  }

  public void setDestination(final String destination) {
    this.destination = destination;
  }

  public void setDirection(final PackerFileProvisionerDirection direction) {
    this.direction = Objects.requireNonNull(direction);
  }

  public void setGenerated(final boolean generated) {
    this.generated = generated;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  @Override
  public void validate() {
  }

}
