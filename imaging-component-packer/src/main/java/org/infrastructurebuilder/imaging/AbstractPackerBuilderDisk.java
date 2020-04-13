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
package org.infrastructurebuilder.imaging;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.Optional;

abstract public class AbstractPackerBuilderDisk extends AbstractPackerBaseObject implements ImageDataDisk {

  private Boolean deleteOnTermination;
  private String deviceName;
  private Boolean encrypted;
  private ImageDataEncryptionIdentifier encryptionIdentifier;
  private int index;
  private Boolean noDevice;
  private String snapshotId;
  private String virtualName;
  private Long volumeSize;
  private String volumeType;

  @Override
  public Optional<String> getDeviceName() {
    return ofNullable(deviceName);
  }

  @Override
  public Optional<ImageDataEncryptionIdentifier> getEncryptionIdentifier() {
    return ofNullable(encryptionIdentifier);
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public Class<?> getLookupClass() {
    return ImageDataDisk.class;
  }

  @Override
  public String getPackerType() {
    return null;
  }

  @Override
  public Optional<String> getSnapshotId() {
    return ofNullable(snapshotId);
  }

  @Override
  public Optional<String> getVirtualName() {
    return ofNullable(virtualName);
  }

  @Override
  public Optional<Long> getVolumeSize() {
    return ofNullable(volumeSize);
  }

  @Override
  public Optional<String> getVolumeType() {
    return ofNullable(volumeType);
  }

  @Override
  public Optional<Boolean> isDeleteOnTermination() {
    return ofNullable(deleteOnTermination);
  }

  @Override
  public Optional<Boolean> isEncrypted() {
    return ofNullable(encrypted);
  }

  @Override
  public Optional<Boolean> isNoDevice() {
    return ofNullable(noDevice);
  }

  public void setDeleteOnTermination(final boolean deleteOnTermination) {
    this.deleteOnTermination = deleteOnTermination;
  }

  public void setDeviceName(final String deviceName) {
    this.deviceName = deviceName;
  }

  public void setEncrypted(final boolean encrypted) {
    this.encrypted = encrypted;
  }

  public void setEncryptionIdentifier(final ImageDataEncryptionIdentifier encryptionIdentifier) {
    this.encryptionIdentifier = requireNonNull(encryptionIdentifier);
  }

  public void setIndex(final int i) {
    index = i;
  }

  public void setNoDevice(final boolean noDevice) {
    this.noDevice = noDevice;
  }

  public void setSnapshotId(final String snapshotId) {
    this.snapshotId = snapshotId;
  }

  public void setVirtualName(final String virtualName) {
    this.virtualName = virtualName;
  }

  public void setVolumeSize(final Long volumeSize) {
    this.volumeSize = requireNonNull(volumeSize);
  }

  public void setVolumeType(final String volumeType) {
    this.volumeType = requireNonNull(volumeType);
  }
}
