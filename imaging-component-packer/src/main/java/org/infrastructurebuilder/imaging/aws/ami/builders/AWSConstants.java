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

import java.util.Arrays;
import java.util.List;

public interface AWSConstants {

  String ACCESS_KEY = "access_key";

  String ACCOUNT_ID = "account_id";

  String AMI_DESCRIPTION = "ami_description";
  String AMI_GROUPS = "ami_groups";
  String AMI_NAME = "ami_name";
  String AMI_PRODUCT_CODES = "ami_product_codes";
  String AMI_REGIONS = "ami_regions";
  String AMI_USERS = "ami_users";
  String AMI_VIRTUALIZATION_TYPE = "ami_virtualization_Type";
  String ASSOCIATE_PUBLIC_IP_ADDRESS = "associate_public_ip_address";

  String AVAILABILITY_ZONE = "availability_zone";
  List<String> COPY_TO = Arrays.asList("us-west-1", "us-east-1", "us-east-2");
  String CUSTOM_ENDPOINT_EC2 = "custom_endpoint_ec2";
  String DISABLE_STOP_INSTANCE = "disable_stop_instance";
  String EBS_OPTIMIZED = "ebs_optimized";
  String ENA_SUPPORT = "ena_support";
  String ENCRYPT_BOOT = "encrypt_boot";
  String FORCE_DELETE_SNAPSHOT = "force_delete_snapshot";
  String FORCE_DEREGISTER = "force_deregister";
  String IAM_INSTANCE_PROFILE = "iam_instance_profile";
  String INSTANCE_TYPE = "instance_type";
  String KMS_KEY_ID = "kms_key_id";
  String LAUNCH_BLOCK_DEVICE_MAPPINGS = "launch_block_device_mappings";
  String MFA_CODE = "mfa_code";
  String PROFILE = "profile";
  String REGION = "region";
  String REGION_KMS_KEY_IDS = "region_kms_key_ids";
  String RUN_TAGS = "run_tags";
  String RUN_VOLUME_TAGS = "run_volume_tags";
  String S3_BUCKET = "s3_bucket";
  String SAF_FILTER_NAME = "name";
  String SAF_FILTER_ROOT_DEVICE_TYPE = "root-device-type";

  String SAF_FILTER_VTYPE = "virtualization-type";

  String SAF_FILTERS = "filters";
  String SAF_MOST_RECENT = "most_recent";

  String SAF_OWNERS = "owners";
  String SECRET_KEY = "secret_key";
  String SECURITY_GROUP_ID = "security_group_id";
  String SHUTDOWN_BEHAVIOR = "shutdown_behavior";
  String SKIP_REGION_VALIDATION = "skip_region_validation";
  String SNAPSHOT_GROUPS = "snapshot_groups";
  String SNAPSHOT_TAGS = "snapshot_tags";
  String SNAPSHOT_USERS = "snapshot_users";
  String SOURCE_AMI = "source_ami";
  String SOURCE_AMI_FILTER = "source_ami_filter";

  String SPOT_PRICE = "spot_price";
  String SPOT_PRICE_AUTO_PRODUCT = "spot_price_auto_product";
  String SRIOV_SUPPORT = "sriov_support";
  String SSH_AUTH_AGENT = "ssh_auth_agent";
  String SSH_KEYPAIR_NAME = "ssh_keypair_name";
  String SUBNET_ID = "subnet_id";
  String TEMPORARY_KEY_PAIR_NAME = "temporary_key_pair_name";
  String TEMPORARY_SECURITY_GROUP_SOURCE_CIDR = "temporary_security_group_cidr";
  String TOKEN = "token";
  String US_WEST_2 = "us-west-2";
  String USER_DATA = "user_data";
  String USER_DATA_FILE = "user_data_file";
  String VPC_ID = "vpc_id";
  String WINDOWS_PASSWORD_TIMEOUT = "windows_password_timeout";

  String X509_CERT_PATH = "x509_cert_path";
  String X509_KEY_PATH = "x509_key_path";

}
