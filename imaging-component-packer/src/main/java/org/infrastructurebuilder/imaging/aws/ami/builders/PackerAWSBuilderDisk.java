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
package org.infrastructurebuilder.imaging.aws.ami.builders;

import static org.infrastructurebuilder.ibr.IBRConstants.AMAZONEBS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.AbstractPackerBuilderDisk;
import org.infrastructurebuilder.imaging.ImageStorage;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONObject;

public final class PackerAWSBuilderDisk extends AbstractPackerBuilderDisk {

  public static final String DEFAULT_AWS_DEVICE_STUB = "/dev/xvd";
  public static final String PACKERAWSBUILDERDISK = "packer-aws-builder-disk";

  public PackerAWSBuilderDisk() {
    super.setVolumeType(AWSBlockMappingType.gp2.name());
    super.setDeleteOnTermination(true);
    super.setEncrypted(false);
    super.setNoDevice(false);
    super.setIndex(0);
  }

  PackerAWSBuilderDisk(final ImageStorage d) {
    this();
    Objects.requireNonNull(d);
    setVolumeSize(d.getSize());
    setDeviceName(d.getDeviceName());
    setVirtualName(d.getVirtualName());
    setIndex(d.getIndex());
    setSnapshotId(d.getSnapshotIdentifier());
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(BM_SNAPSHOT_ID, getSnapshotId())

        .addString(BM_DEVICE_NAME, getDeviceName())

        .addString(BM_VIRTUAL_NAME, getVirtualName())

        .addLong(BM_VOLUME_SIZE, getVolumeSize())

        .addString(BM_VOLUME_TYPE, getVolumeType())

        .addBoolean(BM_DELETE_ON_TERMINATION, isDeleteOnTermination().orElse(false))

        .addBoolean(BM_ENCRYPTED, isEncrypted())

        .addBoolean(BM_NO_DEVICE, isNoDevice())

        .asJSON();
  }

  @Override
  public Optional<String> getDeviceName() {
    return Optional.ofNullable(
        super.getDeviceName().orElse(DEFAULT_AWS_DEVICE_STUB + Character.valueOf((char) ('a' + getIndex())).toString()));
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(AMAZONEBS);
  }

  @Override
  public List<String> getNamedTypes() {
    return PackerAWSBuilder.namedTypes;
  }

  @Override
  public String getType() {
    return AMAZONEBS;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void setDeleteOnTermination(final boolean deleteOnTermination) {
    throw new PackerException("Setting invalid value " + deleteOnTermination);
  }

  @Override
  public void setEncrypted(final boolean encrypted) {
    throw new PackerException("Setting invalid value " + encrypted);
  }

  @Override
  public void setNoDevice(final boolean noDevice) {
    throw new PackerException("Setting invalid value " + noDevice);

  }

  @Override
  public void setVolumeType(final String volumeType) {
    throw new PackerException("Setting invalid value " + volumeType);
  }

  @Override
  public void validate() {
  }

}
