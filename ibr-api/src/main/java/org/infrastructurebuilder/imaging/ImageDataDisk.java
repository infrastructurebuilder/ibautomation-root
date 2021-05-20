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
package org.infrastructurebuilder.imaging;

import java.util.Optional;

import org.infrastructurebuilder.util.core.JSONOutputEnabled;

public interface ImageDataDisk extends JSONOutputEnabled {
  String BM_DELETE_ON_TERMINATION = "delete_on_termination";
  String BM_DEVICE_NAME = "device_name";
  String BM_ENCRYPTED = "encrypted";
  String BM_IOPS = "iops";
  String BM_KMS_KEY_ID = "kms_key_id";
  String BM_NO_DEVICE = "no_device";
  String BM_SNAPSHOT_ID = "snapshot_id";
  String BM_VIRTUAL_NAME = "virtual_name";
  String BM_VOLUME_SIZE = "volume_size";
  String BM_VOLUME_TYPE = "volume_type";

  Optional<String> getDeviceName();

  Optional<ImageDataEncryptionIdentifier> getEncryptionIdentifier();

  int getIndex();

  Optional<String> getSnapshotId();

  String getType();

  Optional<String> getVirtualName();

  Optional<Long> getVolumeSize();

  Optional<String> getVolumeType();

  Optional<Boolean> isDeleteOnTermination();

  Optional<Boolean> isEncrypted();

  Optional<Boolean> isNoDevice();

  boolean isValid();
}
