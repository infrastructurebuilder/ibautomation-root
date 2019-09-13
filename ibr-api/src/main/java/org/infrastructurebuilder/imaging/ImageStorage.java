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

public class ImageStorage {
  private String deviceName;
  private String id;
  private int index;
  private long size;
  private String snapshotIdentifier;
  private String virtualName;

  public String getDeviceName() {
    return deviceName;
  }

  public String getId() {
    return id;
  }

  public int getIndex() {
    return index;
  }

  public long getSize() {
    return size;
  }

  public String getSnapshotIdentifier() {
    return snapshotIdentifier;
  }

  public String getVirtualName() {
    return virtualName;
  }

  public void setDeviceName(final String deviceName) {
    this.deviceName = deviceName;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setSize(final long size) {
    this.size = size;
  }

  public void setSnapshotIdentifier(final String snapshotIdentifier) {
    this.snapshotIdentifier = snapshotIdentifier;
  }

  public void setVirtualName(final String virtualName) {
    this.virtualName = virtualName;
  }

  public ImageStorage withIndex(final int index) {
    this.index = index;
    return this;
  }
}
