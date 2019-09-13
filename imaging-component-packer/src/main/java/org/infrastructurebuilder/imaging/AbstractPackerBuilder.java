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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.imaging.file.PackerFileProvisioner;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONArray;
import org.json.JSONObject;

abstract public class AbstractPackerBuilder<T> extends AbstractPackerBaseObject implements ImageData<T> {

  private String availabilityZone;

  private String buildExecutionName;

  private String content;
  private boolean copyToOtherRegions;
  private List<String> copyToRegions;
  private String credentialsProfile;
  private String description = null;
  private List<ImageDataDisk> disk;
  private String encryptionIdentifier = null;

  private boolean forceDeleteSnapshot = true;
  private boolean forceDeregister = true;
  private Map<String, String> forcedTags;
  private String instanceType;
  private String launchUser;
  private String networkId;
  private String region;
  private Map<String, String> regionalEncryptionIdentifiers = null;
  private String sourceImage = null;
  private String ssh_username;
  private String subnetId;

  private Path targetOutput;
  private final boolean terminateOnShutdown = false;

  private Path userDataFile;

  protected JSONObject sourceFilter = null;

  @Override
  public void addRequiredItemsToFactory(final IBAuthentication a, final PackerFactory<T> f) {
    setInstanceAuthentication(a);
    f.addBuilder(this);
    f.getRequirements().forEach(r -> {
      if (r.isSendToRemote()) {
        final PackerFileProvisioner p = new PackerFileProvisioner();
        p.setBuilders(Collections.emptyList());
        final Path source = r.getFile().get().toAbsolutePath();
        String srcString = source.toString();
        if (Files.isDirectory(source)) {
          srcString += "/";
        }
        p.setSource(srcString);
        p.setDestination(r.getRemote().get());
        f.addProvisioner((PackerProvisioner<T>) p);
      }
    });
  }

  @Override
  public Optional<List<String>> getAccessGroups() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getAvailabilityZone() {
    return Optional.ofNullable(availabilityZone);
  }

  @Override
  public String getBuildExecutionName() {
    if (buildExecutionName == null) {
      buildExecutionName = getId();
    }
    return buildExecutionName;
  }

  @Override
  public Optional<String> getContent() {
    return Optional.ofNullable(content);
  }

  @Override
  public Optional<List<String>> getCopyToRegions() {
    return Optional.ofNullable(copyToRegions);
  }

  @Override
  public Optional<String> getCredentialsProfile() {
    return Optional.ofNullable(credentialsProfile);
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public Optional<List<ImageDataDisk>> getDisk() {
    final List<ImageDataDisk> a = Optional.ofNullable(disk)
        .map(d -> d.stream().filter(f -> !f.isValid()).collect(Collectors.toList())).orElse(Collections.emptyList());
    if (a.size() > 0)

      throw new PackerException("Bad disks " + a);
    return Optional.ofNullable(disk);
  }

  @Override
  public Optional<String> getEncryptionIdentifier() {
    return Optional.ofNullable(encryptionIdentifier);
  }

  @Override
  public Optional<Map<String, String>> getForcedTags() {
    return Optional.ofNullable(forcedTags);
  }

  @Override
  public Optional<String> getImageName() {
    return getArtifact().map(a -> {
      final StringBuilder sb = new StringBuilder();
      sb.append(a.getGroupId()).append("/").append(a.getArtifactId());
      a.getClassifier().ifPresent(c -> {
        sb.append("/").append(c);
      });
      a.getVersion().ifPresent(v -> {
        sb.append("/").append(v);
      });
      if (sb.toString().length() > 128)
        throw new PackerException("Image name '" + sb.toString() + "' is too long.  Sorry.");
      return sb.toString();
    });

  }

  @Override
  public Optional<String> getInstanceType() {
    return Optional.ofNullable(instanceType);
  }

  @Override
  public Optional<String> getLaunchUser() {
    return Optional.ofNullable(launchUser);
  }

  @Override
  public Class<?> getLookupClass() {
    return ImageData.class;
  }

  @Override
  public Optional<String> getNetworkId() {
    return Optional.ofNullable(networkId);
  }

  @Override
  public Optional<String> getProvisioningUser() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getRegion() {
    return Optional.ofNullable(region);
  }

  @Override
  public Optional<Map<String, String>> getRegionalEncryptionIdentifiers() {

    final Map<String, String> map = Optional.ofNullable(regionalEncryptionIdentifiers).orElse(new HashMap<>());
    getCopyToRegions().ifPresent(r -> {
      r.stream().forEach(reg -> {
        if (!map.containsKey(reg)) {
          map.put(reg, "");
        }
      });
    });
    return Optional.ofNullable(map.isEmpty() ? null : map);
  }

  @Override
  public Optional<Map<String, Object>> getSourceFilter() {
    return Optional.ofNullable(sourceFilter).map(JSONObject::toMap);
  }

  @Override
  public Optional<String> getSourceImage() {
    return Optional.ofNullable(sourceImage);
  }

  @Override
  public Optional<String> getSSHUsername() {
    return Optional.ofNullable(ssh_username);
  }

  @Override
  public Optional<String> getSubnetId() {
    return Optional.ofNullable(subnetId);
  }

  @Override
  public Map<String, String> getTags() {
    final Map<String, String> daTags = super.getTags();
    getForcedTags().ifPresent(daTags::putAll);
    return daTags;
  }

  @Override
  public Optional<Path> getTargetOutput() {
    return Optional.ofNullable(targetOutput);
  }

  @Override
  public Optional<String> getUserData() {
    return getUserDataFile().map(f -> IBException.cet.withReturningTranslation(() -> IBUtils.readFile(f)));
  }

  @Override
  public Optional<Path> getUserDataFile() {
    return Optional.ofNullable(userDataFile);
  }

  @Override
  public boolean isForceDeleteSnapshot() {
    return forceDeleteSnapshot;
  }

  @Override
  public boolean isForceDeregister() {
    return forceDeregister;
  }

  public boolean isTerminateOnShutdown() {
    return terminateOnShutdown;
  }

  @Override
  public void setAccessGroups(final List<String> accessGroups) {

  }

  @Override
  public void setAvailabilityZone(final String availabilityZone) {
    this.availabilityZone = Objects.requireNonNull(availabilityZone);
  }

  public void setBuildExecutionName(final String buildExecutionName) {
    this.buildExecutionName = buildExecutionName;
  }

  @Override
  public void setContent(final String content) {
    this.content = Objects.requireNonNull(content);
  }

  @Override
  public void setCopyToOtherRegions(final boolean copyToOtherRegions) {
    this.copyToOtherRegions = copyToOtherRegions;
  }

  @Override
  public void setCopyToRegions(final List<String> copyToRegions) {
    this.copyToRegions = Objects.requireNonNull(copyToRegions);
  }

  @Override
  public void setCredentialsProfile(final String credentialsProfile) {
    this.credentialsProfile = Objects.requireNonNull(credentialsProfile);
  }

  @Override
  public void setDescription(final String description) {
    this.description = Objects.requireNonNull(description);
  }

  @Override
  public void setDisk(final List<ImageDataDisk> disk) {
    this.disk = Objects.requireNonNull(disk);
    final List<ImageDataDisk> d1 = Optional.ofNullable(disk)

        .map(d -> d.stream().filter(dd -> !getNamedTypes().contains(dd.getType())).collect(Collectors.toList()))
        .orElse(new ArrayList<>());
    if (!d1.isEmpty())
      throw new PackerException("Disks do not have managed type for this builder :"
          + new JSONArray(d1.stream().map(ImageDataDisk::asJSON).collect(Collectors.toList())));
  }

  @Override
  public void setEncryptionId(final String cryptoId) {
    encryptionIdentifier = Objects.requireNonNull(cryptoId);
  }

  @Override
  public void setForceDeleteSnapshot(final boolean forceDeleteSnapshot) {
    this.forceDeleteSnapshot = forceDeleteSnapshot;
  }

  @Override
  public void setForceDeregister(final boolean forceDeregister) {
    this.forceDeregister = forceDeregister;
  }

  @Override
  public void setForcedTags(final Map<String, String> forcedTags) {
    Map<String, String> n = Objects.requireNonNull(forcedTags);
    if (n.size() == 0) {
      n = null;
    }
    this.forcedTags = n;
  }

  @Override
  public void setInstanceType(final String instanceType) {
    this.instanceType = Objects.requireNonNull(instanceType);
  }

  @Override
  public void setLaunchUser(final String launchUser) {
    this.launchUser = Objects.requireNonNull(launchUser);
  }

  @Override
  public void setNetworkId(final String networkId) {
    this.networkId = Objects.requireNonNull(networkId);
  }

  @Override
  public void setRegion(final String region) {
    this.region = Objects.requireNonNull(region);
  }

  @Override
  public void setRegionalEncryptionIdentifiers(final Map<String, String> regionalEncryptionIdentifiers) {
    this.regionalEncryptionIdentifiers = Objects.requireNonNull(regionalEncryptionIdentifiers);
  }

  @Override
  public void setSourceFilter(final Map<String, Object> sourceFilter) {
    this.sourceFilter = new JSONObject(Objects.requireNonNull(sourceFilter));
    sourceImage = null;
  }

  @Override
  public void setSourceImage(final String sourceImage) {
    this.sourceImage = Objects.requireNonNull(sourceImage);
    sourceFilter = null;
  }

  public void setSSHUsername(final String ssh_username) {
    this.ssh_username = Objects.requireNonNull(ssh_username);
  }

  @Override
  public void setSubnetId(final String subnetId) {
    this.subnetId = Objects.requireNonNull(subnetId);
  }

  @Override
  public void setTargetOutput(final Path targetOutput) {
    this.targetOutput = Objects.requireNonNull(targetOutput);
  }

  @Override
  public void setUserDataFile(final Path userDataFile) {
    this.userDataFile = Objects.requireNonNull(userDataFile);
  }

  @Override
  public void updateBuilderWithInstanceData(final PackerSizing size, final IBAuthentication a,
      final Optional<ImageBuildResult> manifest, final List<ImageStorage> disks, final Optional<Type> builderData) {

  }

  @Override
  public void validate() {
  }

  protected Optional<JSONArray> getBlockDeviceMappings() {
    return getDisk().map(dl -> dl.stream().map(d -> d.asJSON()).collect(Collectors.toList()))
        .map(al -> new JSONArray(al));
  }

  protected boolean isCopyToOtherRegions() {
    return copyToOtherRegions;
  }
}
