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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.ibr.IBRConstants;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONObject;

@Named("fake")
@Typed(ImageData.class)
public class DefaultFakeImageData extends FakePackerBaseObject implements ImageData {

  private Optional<String> provUser = Optional.empty();

  public DefaultFakeImageData(final String type, final Optional<String> provUser) {
    super(type);
    this.provUser = provUser;
  }

  @Override
  public void addRequiredItemsToFactory(final IBAuthentication a, final PackerFactory f) {

  }

  @Override
  public Optional<List<String>> getAccessGroups() {

    return null;
  }

  @Override
  public Optional<String> getAuthType() {

    return null;
  }

  @Override
  public Optional<String> getAvailabilityZone() {

    return null;
  }

  @Override
  public String getBuildExecutionName() {
    return "test";
  }

  @Override
  public Optional<String> getContent() {

    return null;
  }

  @Override
  public Optional<List<String>> getCopyToRegions() {

    return null;
  }

  @Override
  public Optional<String> getCredentialsProfile() {

    return null;
  }

  @Override
  public Optional<String> getDescription() {

    return null;
  }

  @Override
  public Optional<String> getEncryptionIdentifier() {

    return null;
  }

  @Override
  public Optional<Map<String, String>> getForcedTags() {

    return null;
  }

  @Override
  public Optional<String> getImageName() {

    return null;
  }

  @Override
  public Optional<String> getInstanceType() {

    return null;
  }

  @Override
  public Optional<String> getLaunchUser() {

    return null;
  }

  @Override
  public Optional<String> getNetworkId() {

    return null;
  }

  @Override
  public String getPackerType() {
    return IBRConstants.AMAZONEBS;
  }

  @Override
  public Optional<String> getProvisioningUser() {
    return provUser;
  }

  @Override
  public Optional<String> getRegion() {

    return null;
  }

  @Override
  public Optional<Map<String, String>> getRegionalEncryptionIdentifiers() {

    return null;
  }

  @Override
  public List<String> getSizes() {

    return null;
  }

  @Override
  public Optional<Map<String, Object>> getSourceFilter() {

    return null;
  }

  @Override
  public Optional<String> getSourceImage() {

    return null;
  }

  @Override
  public Optional<String> getSSHUsername() {

    return null;
  }

  @Override
  public Optional<String> getSubnetId() {

    return null;
  }

  @Override
  public Optional<Path> getTargetOutput() {

    return null;
  }

  @Override
  public Optional<String> getUserData() {

    return null;
  }

  @Override
  public Optional<Path> getUserDataFile() {

    return null;
  }

  @Override
  public boolean isForceDeleteSnapshot() {

    return false;
  }

  @Override
  public boolean isForceDeregister() {

    return false;
  }

  @Override
  public ImageBuildResult mapBuildResult(final JSONObject a) {

    return null;
  }

  @Override
  public void setAccessGroups(final List<String> accessGroups) {

  }

  @Override
  public void setAvailabilityZone(final String availabilityZone) {

  }

  @Override
  public void setContent(final String content) {

  }

  @Override
  public void setCopyToOtherRegions(final boolean copyToOtherRegions) {

  }

  @Override
  public void setCopyToRegions(final List<String> copyToRegions) {

  }

  @Override
  public void setCredentialsProfile(final String credentialsProfile) {

  }

  @Override
  public void setDescription(final String description) {

  }

  @Override
  public void setDisk(final List<ImageDataDisk> disk) {

  }

  @Override
  public void setEncryptionId(final String cryptoId) {

  }

  @Override
  public void setForceDeleteSnapshot(final boolean forceDeleteSnapshot) {

  }

  @Override
  public void setForceDeregister(final boolean forceDeregister) {

  }

  @Override
  public void setForcedTags(final Map<String, String> forcedTags) {

  }

  @Override
  public void setInstanceType(final String instanceType) {

  }

  @Override
  public void setLaunchUser(final String launchUser) {

  }

  @Override
  public void setNetworkId(final String networkId) {

  }

  @Override
  public void setRegion(final String region) {

  }

  @Override
  public void setRegionalEncryptionIdentifiers(final Map<String, String> regionalEncryptionIdentifiers) {

  }

  @Override
  public void setSourceFilter(final Map<String, Object> sourceFilter) {

  }

  @Override
  public void setSourceImage(final String sourceImage) {

  }

  @Override
  public void setSubnetId(final String subnetId) {

  }

  @Override
  public void setTargetOutput(final Path targetOutput) {

  }

  @Override
  public void setUserDataFile(final Path userDataFile) {

  }

  @Override
  public void updateBuilderWithInstanceData(final String size, final IBAuthentication a,
      final Optional<ImageBuildResult> manifest, final List<ImageStorage> list, final Optional<Type> builderData) {

  }

  @Override
  public IBRDialect getDialect() {
    return null;
  }
}
