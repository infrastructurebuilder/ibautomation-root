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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONObject;

public interface ImageData<T> extends ImageBaseObject {
  void addRequiredItemsToFactory(IBAuthentication a, PackerFactory<T> f);

  Optional<List<String>> getAccessGroups();

  @Override
  Optional<GAV> getArtifact();

  Optional<String> getAuthType();

  Optional<String> getAvailabilityZone();

  String getBuildExecutionName();

  Optional<String> getContent();

  Optional<List<String>> getCopyToRegions();

  Optional<String> getCredentialsProfile();

  Optional<String> getDescription();

  Optional<String> getEncryptionIdentifier();

  Optional<Map<String, String>> getForcedTags();

  Optional<String> getImageName();

  Optional<String> getInstanceType();

  Optional<String> getLaunchUser();

  Optional<String> getNetworkId();

  Optional<String> getProvisioningUser();

  Optional<String> getRegion();

  Optional<Map<String, String>> getRegionalEncryptionIdentifiers();

  List<PackerSizing> getSizes();

  Optional<Map<String, Object>> getSourceFilter();

  Optional<String> getSourceImage();

  Optional<String> getSSHUsername();

  Optional<String> getSubnetId();

  Optional<Path> getTargetOutput();

  Optional<String> getUserData();

  Optional<Path> getUserDataFile();

  boolean isForceDeleteSnapshot();

  boolean isForceDeregister();

  ImageBuildResult mapBuildResult(JSONObject a);

  void setAccessGroups(List<String> accessGroups);

  @Override
  void setArtifact(GAV artifact);

  void setAvailabilityZone(String availabilityZone);

  void setContent(String content);

  void setCopyToOtherRegions(boolean copyToOtherRegions);

  void setCopyToRegions(List<String> copyToRegions);

  void setCredentialsProfile(String credentialsProfile);

  void setDescription(String description);

  void setDisk(List<ImageDataDisk> disk);

  void setEncryptionId(String cryptoId);

  void setForceDeleteSnapshot(boolean forceDeleteSnapshot);

  void setForceDeregister(boolean forceDeregister);

  void setForcedTags(Map<String, String> forcedTags);

  void setInstanceType(String instanceType);

  void setLaunchUser(String launchUser);

  void setNetworkId(String networkId);

  void setRegion(String region);

  void setRegionalEncryptionIdentifiers(Map<String, String> regionalEncryptionIdentifiers);

  void setSourceFilter(Map<String, Object> sourceFilter);

  void setSourceImage(String sourceImage);

  void setSubnetId(String subnetId);

  void setTargetOutput(Path targetOutput);

  void setUserDataFile(Path userDataFile);

  void updateBuilderWithInstanceData(PackerSizing size, IBAuthentication a, Optional<ImageBuildResult> manifest,
      List<ImageStorage> list, Optional<Type> updateData);

  @Override
  void validate();

}
