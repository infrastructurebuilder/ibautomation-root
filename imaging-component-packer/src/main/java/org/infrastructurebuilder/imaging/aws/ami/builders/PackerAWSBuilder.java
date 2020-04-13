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
package org.infrastructurebuilder.imaging.aws.ami.builders;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.ibr.IBRConstants.AMAZONEBS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ARTIFACT_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SSH_USERNAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TAGS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.AMI_DESCRIPTION;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.AMI_NAME;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.AMI_REGIONS;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.AVAILABILITY_ZONE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.COPY_TO;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.FORCE_DELETE_SNAPSHOT;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.FORCE_DEREGISTER;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.IAM_INSTANCE_PROFILE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.INSTANCE_TYPE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.KMS_KEY_ID;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.LAUNCH_BLOCK_DEVICE_MAPPINGS;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.PROFILE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.REGION;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.SHUTDOWN_BEHAVIOR;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.SPOT_PRICE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.SPOT_PRICE_AUTO_PRODUCT;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.SUBNET_ID;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.USER_DATA_FILE;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.US_WEST_2;
import static org.infrastructurebuilder.imaging.aws.ami.builders.AWSConstants.VPC_ID;
import static org.jooq.lambda.tuple.Tuple.tuple;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Named;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.AbstractPackerBuildResult;
import org.infrastructurebuilder.imaging.AbstractPackerBuilder;
import org.infrastructurebuilder.imaging.IBRDialectMapper;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.ImageDataDisk;
import org.infrastructurebuilder.imaging.ImageStorage;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerSizing2;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.jooq.lambda.tuple.Tuple2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(AMAZONEBS)
@Typed(ImageData.class)
public class PackerAWSBuilder extends AbstractPackerBuilder implements Initializable {
  private static final String EC2_USER               = "ec2-user";
  private static final String SOURCE_AMI_NAME        = "{{ .SourceAMIName }}";
  private static final String SOURCE_AMI_VAL         = "{{ .SourceAMI }}";
  private static final String PACKER_SOURCE_AMI_NAME = "SourceAMIName";
  private static final String PACKER_SOURCE_AMI      = "SourceAMI";
  private static final String SOURCE_AMI_FILTER      = "source_ami_filter";
  private static final String SOURCE_AMI             = "source_ami";
  private static final String AUTO                   = "auto";

  public final class PackerAWSBuildResult extends AbstractPackerBuildResult {

    public PackerAWSBuildResult(final JSONObject j) {
      super(j);
    }

    @Override
    public Optional<String> getArtifactInfo() {
      return ofNullable(getJSON().optString(ARTIFACT_ID)).map(a -> {
        final JSONObject artifactInfo = new JSONObject();
        for (final String b : asList(a.split(","))) {
          final String[] c = b.split(":");
          artifactInfo.put(c[0], c[1]);
        }
        return artifactInfo.toString();
      });
    }
  }

  public final static String AWS_AMAZON_EBS_PACKER_BUILDER    = "aws-ebs-packer-builder";
  public final static String DEFAULT_AMZN_LINUX_AMI_STRING    = "amzn-ami*-ebs";
  public final static String DEFAULT_AMZN_LINUX_DL_AMI_STRING = "Deep Learning Base AMI (Amazon Linux) Version 9.0";

  public final static String DEFAULT_AMZN2_LINUX_AMI_STRING = "amzn2-ami*-ebs";
  public static final String DEFAULT_INSTANCE_TYPE          = "t2.micro";
  public static final String DEFAULT_REGION                 = "us-west-2";

  private final static Logger log = LoggerFactory.getLogger(PackerAWSBuilder.class);

  private static final String PROVISIONING_USER    = EC2_USER;
  static final List<String>   namedTypes           = asList(AMAZONEBS);
  private static final String AWS_AMI_DIALECT_TYPE = "aws-ami";;

//  static Map<PackerSizing, String> sizeToInstanceMap = new HashMap<PackerSizing, String>() {
//    private static final long serialVersionUID = -7242895914690962787L;
//
//    {
//      put(PackerSizing.small, DEFAULT_INSTANCE_TYPE);
//      put(PackerSizing.medium, "t2.medium");
//      put(PackerSizing.large, "t2.large");
//      put(PackerSizing.gpu, "p3.2xlarge");
//      put(PackerSizing.gpularge, "p3.16xlarge");
//      put(PackerSizing.stupid, "c5d.18xlarge");
//    }
//  };
//
  static Map<String, Optional<List<ImageDataDisk>>> sizeToStorageMap = new HashMap<String, Optional<List<ImageDataDisk>>>() {
    private static final long serialVersionUID = -5278926873567104925L;

    {
      put(PackerSizing2.small.name(), empty());
      put(PackerSizing2.medium.name(), empty());
      put(PackerSizing2.large.name(), empty());
      put(PackerSizing2.gpu.name(), empty());
      put(PackerSizing2.gpularge.name(), empty());
      put(PackerSizing2.stupid.name(), empty());

    }
  };

  private static JSONObject getDefaultSourceFilter(final String name, final Optional<AWSVirtType> v,
      final Optional<AWSDeviceType> deviceType) {
    return JSONBuilder.newInstance().addBoolean("most_recent", true)
        .addJSONArray("owners", new JSONArray(asList("self", "amazon")))
        .addJSONObject("filters",
            JSONBuilder.newInstance().addString("virtualization-type", v.map(AWSVirtType::name))
                .addString("root-device-type", deviceType.map(AWSDeviceType::getTypeString))
                .addString("name", requireNonNull(name)).asJSON())
        .asJSON();
  }

  private AWSDeviceType AWS_DeviceType = null;

  private final String aws_source_name = DEFAULT_AMZN_LINUX_AMI_STRING;

  private String       AWS_SpotPrice;
  private final String AWS_SpotPriceAutoProduct = "Linux/UNIX (Amazon VPC)";
  private AWSVirtType  AWS_VirtType             = null;

  public PackerAWSBuilder(IBRDialectMapper mapper) {
    setDialect(requireNonNull(mapper, "IBRDialectMapper").getDialectFor(AWS_AMI_DIALECT_TYPE, empty())
        .orElseThrow(() -> new PackerException("No AWS Supplier!  FIXME!")));
    setRegion(US_WEST_2);
    if (isCopyToOtherRegions()) {
      setCopyToRegions(COPY_TO);
    }

  }

  @Override
  public void addRequiredItemsToFactory(IBAuthentication a, PackerFactory f) {
    super.addRequiredItemsToFactory(a, f);
  }

  @Override
  public JSONObject asJSON() {
    validate();
    final JSONObject j = JSONBuilder.newInstance().addString(TYPE, getPackerType())
        .addString(SSH_USERNAME, getSSHUsername()).addString(NAME, getBuildExecutionName())
        .addString(AMI_NAME, getImageName().orElseThrow(() -> new PackerException("No imageName available")))
        .addString(REGION, getRegion().orElse(DEFAULT_REGION)).addString(INSTANCE_TYPE, getInstanceType())
        .addString(PROFILE, getCredentialsProfile()).addString(KMS_KEY_ID, getEncryptionIdentifier())
        .addString(IAM_INSTANCE_PROFILE, getLaunchUser()).addString(SUBNET_ID, getSubnetId())
        .addString(AVAILABILITY_ZONE, getAvailabilityZone()).addString(VPC_ID, getNetworkId())
        .addString(AMI_DESCRIPTION, getDescription()).addBoolean(FORCE_DELETE_SNAPSHOT, isForceDeleteSnapshot())
        .addBoolean(FORCE_DEREGISTER, isForceDeregister()).addMapStringString(TAGS, getTags())
        .addString(SHUTDOWN_BEHAVIOR, isTerminateOnShutdown() ? "terminate" : "stop")
        .addString(SPOT_PRICE, getSpotPrice()).addString(SPOT_PRICE_AUTO_PRODUCT, getSpotPriceAutoProduct())
        .addString(USER_DATA_FILE, getUserDataFile().map(Path::toAbsolutePath).map(Path::toString))
        .addJSONArray(LAUNCH_BLOCK_DEVICE_MAPPINGS, getBlockDeviceMappings())
        .addListString(AMI_REGIONS, getCopyToRegions()).asJSON();
    getSource().ifPresent(s -> j.put(s.v1(), s.v2()));
    return j;
  }

  @Override
  public Optional<String> getAuthType() {
    return Optional.of(AMAZONEBS);
  }

  @Override
  public Optional<List<ImageDataDisk>> getDisk() {

    return super.getDisk();
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(AMAZONEBS);
  }

  @Override
  public List<String> getNamedTypes() {
    return namedTypes;
  }

  @Override
  public String getPackerType() {
    return AMAZONEBS;
  }

  @Override
  public Optional<String> getProvisioningUser() {
    return Optional.of(PROVISIONING_USER);
  }

  @Override
  public List<String> getSizes() {
    return asList(PackerSizing2.values()).stream().map(PackerSizing2::name).collect(toList());
  }

  public String getSizeToInstanceMapping(final String size) {

    return getDialect().getImageSizeIdentifierFor(size).orElseThrow(
        () -> new PackerException("No mapping for " + size + " in technology " + getDialect().getDialect()));
  }

  public Map<String, Optional<List<ImageDataDisk>>> getSizeToStorageMap() {
    return sizeToStorageMap;
  }

  @Override
  public Optional<Map<String, Object>> getSourceFilter() {
    return ofNullable(
        ofNullable(sourceFilter).orElse(getDefaultSourceFilter(getSourceFilterName(), getVirtType(), getDeviceType())))
            .map(JSONObject::toMap);
  }

  public Optional<String> getSpotPrice() {
    return ofNullable(AWS_SpotPrice);
  }

  public Optional<String> getSpotPriceAutoProduct() {
    return getSpotPrice().filter(a -> a.equals(AUTO)).flatMap(b -> ofNullable(AWS_SpotPriceAutoProduct));
  }

  @Override
  public void initialize() throws InitializationException {
    ofNullable(getLog())
        .ifPresent(myLog -> myLog.debug("Initializing with " + isCopyToOtherRegions() + " and " + COPY_TO));

  }

  @Override
  public final ImageBuildResult mapBuildResult(final JSONObject a) {
    return new PackerAWSBuildResult(a);
  }

  @Override
  public void setCopyToOtherRegions(final boolean copyToOtherRegions) {

    super.setCopyToOtherRegions(copyToOtherRegions);
    et.withTranslation(() -> initialize());
  }

  public void setDeviceType(final String deviceType) {
    AWS_DeviceType = AWSDeviceType.valueOf(requireNonNull(deviceType));
  }

  public void setSpotPrice(String spot) {
    final String s = spot;
    if (spot != null) {
      switch (spot) {
        case "0":
          spot = null;
          break;
        case AUTO:
          break;
        default:
          spot = et.withReturningTranslation(() -> new Long(s).toString());
      }
    }
    AWS_SpotPrice = spot;
  }

  public void setVirtType(final String virtType) {
    AWS_VirtType = AWSVirtType.valueOf(requireNonNull(virtType));
  }

  @Override
  public void updateBuilderWithInstanceData(final String size, final IBAuthentication a,
      final Optional<ImageBuildResult> manifest, final List<ImageStorage> disks, final Optional<Type> builderData) {
    super.updateBuilderWithInstanceData(size, a, manifest, disks, builderData);
    final Map<String, String> fTags = getForcedTags().orElse(new HashMap<>());
    fTags.put(PACKER_SOURCE_AMI, SOURCE_AMI_VAL);
    fTags.put(PACKER_SOURCE_AMI_NAME, SOURCE_AMI_NAME);
    setForcedTags(fTags);
    setSSHUsername(getInstanceTypeMappedUsername(size));
    setInstanceType(getSizeToInstanceMapping(size));

    setCredentialsProfile(a.getId());

    setDisk(requireNonNull(disks).stream().map(PackerAWSBuilderDisk::new).collect(toList()));

    requireNonNull(manifest).ifPresent(m -> {
      final Optional<JSONObject> j = m.getArtifactInfo().map(JSONObject::new);
      j.ifPresent(lr -> {
        final String x = lr.getString(US_WEST_2);
        log.info("Imagesource -> " + x);
        setSourceImage(x);
      });

    });
    requireNonNull(builderData).ifPresent(bData -> {

    });
  }

  @Override
  public void validate() {
    if (getNetworkId().isPresent() && !getSubnetId().isPresent())
      throw new PackerException("Setting VPC requires setting subnet");
  }

  private Optional<Tuple2<String, Object>> getSource() {
    return ofNullable(

        getSourceImage().map(i -> tuple(SOURCE_AMI, (Object) i))

            .orElse(

                getSourceFilter().map(a -> new JSONObject(a)).map(i -> tuple(SOURCE_AMI_FILTER, (Object) i))

                    .orElse(null)));
  }

  protected String getInstanceTypeMappedUsername(final String size) {

    return EC2_USER;
  }

  Optional<AWSDeviceType> getDeviceType() {
    return ofNullable(AWS_DeviceType);
  }

  String getSourceFilterName() {
    return ofNullable(aws_source_name).orElse(DEFAULT_AMZN_LINUX_AMI_STRING);
  }

  Optional<AWSVirtType> getVirtType() {
    return ofNullable(AWS_VirtType);
  }

}
