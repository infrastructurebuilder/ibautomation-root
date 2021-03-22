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

import static java.io.File.separator;
import static java.nio.file.Files.isDirectory;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.IBUtils.readFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.file.PackerFileProvisioner;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONArray;
import org.json.JSONObject;

abstract public class AbstractPackerBuilder extends AbstractPackerBaseObject implements ImageData {

  private String              availabilityZone;
  private String              buildExecutionName;
  private String              content;
  private boolean             copyToOtherRegions;
  private List<String>        copyToRegions;
  private String              credentialsProfile;
  private String              description                   = null;
  private List<ImageDataDisk> disk;
  private String              encryptionIdentifier          = null;
  private boolean             forceDeleteSnapshot           = true;
  private boolean             forceDeregister               = true;
  private Map<String, String> forcedTags;
  private String              instanceType;
  private String              launchUser;
  private String              networkId;
  private String              region;
  private Map<String, String> regionalEncryptionIdentifiers = null;
  private String              sourceImage                   = null;
  private String              ssh_username;
  private String              subnetId;
  private IBRDialect          dialect;
  private Path                targetOutput;
  private final boolean       terminateOnShutdown           = false;
  private Path                userDataFile;
  protected JSONObject        sourceFilter                  = null;

  @SuppressWarnings("unchecked")
  @Override
  public void addRequiredItemsToFactory(final IBAuthentication a, final PackerFactory f) {
    setInstanceAuthentication(a);
    f.addBuilder(this);
    f.getRequirements().forEach(r -> {
      if (r.isSendToRemote()) {
        final PackerFileProvisioner p = new PackerFileProvisioner();
        p.setBuilders(emptyList());
        final Path source = r.getFile().get().toAbsolutePath();
        String srcString = source.toString();
        if (isDirectory(source) && !srcString.endsWith(separator)) {
          srcString += separator;
        }
        p.setSource(srcString);
        p.setDestination(r.getRemote().get());
        f.addProvisioner((PackerProvisioner) p);
      }
    });
  }

  @Override
  public Optional<List<String>> getAccessGroups() {
    return empty();
  }

  @Override
  public Optional<String> getAvailabilityZone() {
    return ofNullable(availabilityZone);
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
    return ofNullable(content);
  }

  @Override
  public Optional<List<String>> getCopyToRegions() {
    return ofNullable(copyToRegions);
  }

  @Override
  public Optional<String> getCredentialsProfile() {
    return ofNullable(credentialsProfile);
  }

  @Override
  public Optional<String> getDescription() {
    return ofNullable(description);
  }

  public Optional<List<ImageDataDisk>> getDisk() {
    final List<ImageDataDisk> a = ofNullable(disk).map(d -> d.stream().filter(f -> !f.isValid()).collect(toList()))
        .orElse(emptyList());
    if (a.size() > 0)

      throw new PackerException("Bad disks " + a);
    return ofNullable(disk);
  }

  @Override
  public Optional<String> getEncryptionIdentifier() {
    return ofNullable(encryptionIdentifier);
  }

  @Override
  public Optional<Map<String, String>> getForcedTags() {
    return ofNullable(forcedTags);
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
    return ofNullable(instanceType);
  }

  @Override
  public Optional<String> getLaunchUser() {
    return ofNullable(launchUser);
  }

  @Override
  public Class<?> getLookupClass() {
    return ImageData.class;
  }

  @Override
  public Optional<String> getNetworkId() {
    return ofNullable(networkId);
  }

  @Override
  public Optional<String> getProvisioningUser() {
    return empty();
  }

  @Override
  public Optional<String> getRegion() {
    return ofNullable(region);
  }

  @Override
  public Optional<Map<String, String>> getRegionalEncryptionIdentifiers() {
    final Map<String, String> map = ofNullable(regionalEncryptionIdentifiers).orElse(new HashMap<>());
    getCopyToRegions().ifPresent(r -> r.stream().forEach(reg -> map.computeIfAbsent(reg, (k) -> "")));
    return ofNullable(map.isEmpty() ? null : map);
  }

  @Override
  public Optional<Map<String, Object>> getSourceFilter() {
    return ofNullable(sourceFilter).map(JSONObject::toMap);
  }

  @Override
  public Optional<String> getSourceImage() {
    return ofNullable(sourceImage);
  }

  @Override
  public Optional<String> getSSHUsername() {
    return ofNullable(ssh_username);
  }

  @Override
  public Optional<String> getSubnetId() {
    return ofNullable(subnetId);
  }

  @Override
  public Map<String, String> getTags() {
    final Map<String, String> daTags = super.getTags();
    getForcedTags().ifPresent(super.getTags()::putAll);
    return daTags;
  }

  @Override
  public Optional<Path> getTargetOutput() {
    return ofNullable(targetOutput);
  }

  @Override
  public Optional<String> getUserData() {
    return getUserDataFile().map(f -> cet.withReturningTranslation(() -> readFile(f)));
  }

  @Override
  public Optional<Path> getUserDataFile() {
    return ofNullable(userDataFile);
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
    this.availabilityZone = requireNonNull(availabilityZone);
  }

  public void setBuildExecutionName(final String buildExecutionName) {
    this.buildExecutionName = buildExecutionName;
  }

  @Override
  public void setContent(final String content) {
    this.content = requireNonNull(content);
  }

  @Override
  public void setCopyToOtherRegions(final boolean copyToOtherRegions) {
    this.copyToOtherRegions = copyToOtherRegions;
  }

  @Override
  public void setCopyToRegions(final List<String> copyToRegions) {
    this.copyToRegions = requireNonNull(copyToRegions);
  }

  @Override
  public void setCredentialsProfile(final String credentialsProfile) {
    this.credentialsProfile = requireNonNull(credentialsProfile);
  }

  @Override
  public void setDescription(final String description) {
    this.description = requireNonNull(description);
  }

  @Override
  public void setDisk(final List<ImageDataDisk> disk) {
    this.disk = requireNonNull(disk);
    final List<ImageDataDisk> d1 = ofNullable(disk)

        .map(d -> d.stream().filter(dd -> !getNamedTypes().contains(dd.getType())).collect(toList()))
        .orElse(new ArrayList<>());
    if (!d1.isEmpty())
      throw new PackerException("Disks do not have managed type for this builder :"
          + new JSONArray(d1.stream().map(ImageDataDisk::asJSON).collect(toList())));
  }

  @Override
  public void setEncryptionId(final String cryptoId) {
    encryptionIdentifier = requireNonNull(cryptoId);
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
    Map<String, String> n = requireNonNull(forcedTags);
    if (n.size() == 0) {
      n = null;
    }
    this.forcedTags = n;
  }

  @Override
  public void setInstanceType(final String instanceType) {
    this.instanceType = requireNonNull(instanceType);
  }

  @Override
  public void setLaunchUser(final String launchUser) {
    this.launchUser = requireNonNull(launchUser);
  }

  @Override
  public void setNetworkId(final String networkId) {
    this.networkId = requireNonNull(networkId);
  }

  @Override
  public void setRegion(final String region) {
    this.region = requireNonNull(region);
  }

  @Override
  public void setRegionalEncryptionIdentifiers(final Map<String, String> regionalEncryptionIdentifiers) {
    this.regionalEncryptionIdentifiers = requireNonNull(regionalEncryptionIdentifiers);
  }

  @Override
  public void setSourceFilter(final Map<String, Object> sourceFilter) {
    this.sourceFilter = new JSONObject(requireNonNull(sourceFilter));
    sourceImage = null;
  }

  @Override
  public void setSourceImage(final String sourceImage) {
    this.sourceImage = requireNonNull(sourceImage);
    sourceFilter = null;
  }

  public void setSSHUsername(final String ssh_username) {
    this.ssh_username = requireNonNull(ssh_username);
  }

  @Override
  public void setSubnetId(final String subnetId) {
    this.subnetId = requireNonNull(subnetId);
  }

  @Override
  public void setTargetOutput(final Path targetOutput) {
    this.targetOutput = requireNonNull(targetOutput);
  }

  @Override
  public void setUserDataFile(final Path userDataFile) {
    this.userDataFile = requireNonNull(userDataFile);
  }

  @Override
  public void updateBuilderWithInstanceData(final String size, final IBAuthentication a,
      final Optional<ImageBuildResult> manifest, final List<ImageStorage> disks, final Optional<Type> builderData) {

  }

  @Override
  public void validate() {
  }

  protected Optional<JSONArray> getBlockDeviceMappings() {
    return getDisk().map(dl -> dl.stream().map(d -> d.asJSON()).collect(toList())).map(al -> new JSONArray(al));
  }

  protected boolean isCopyToOtherRegions() {
    return copyToOtherRegions;
  }

  public void setDialect(IBRDialect dialect) {
    this.dialect = dialect;
  }

  @Override
  public IBRDialect getDialect() {
    return dialect;
  }
}
