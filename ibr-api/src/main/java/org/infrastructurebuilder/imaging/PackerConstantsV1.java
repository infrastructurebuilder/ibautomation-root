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

import static java.time.Duration.ofMillis;
import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.List;

public interface PackerConstantsV1 {

  String       ARTIFACT                   = "artifact";
  String       ARTIFACT_COUNT             = "artifact-count";
  String       ARTIFACT_ID                = "artifact_id";
  String       BUILD_COMMAND              = "build";
  String       BUILD_TIME                 = "build_time";
  String       BUILDER                    = "builder";
  String       BUILDER_ID                 = "builder-id";
  String       BUILDER_TYPE               = "builder_type";
  String       BUILDS                     = "builds";
  String       CHECKSUM                   = "checksum";
  String       CHECKSUM_TYPES             = "checksum_types";
  String       COLOR_FALSE                = "-color=false";
  String       COMMIT                     = "commit";
  String       COMPRESS                   = "compress";
  String       COMPRESSION_LEVEL          = "compression_level";
  String       CONTENT                    = "content";
  String       DATA                       = "data";
  String       DEFAULT                    = "default";
  String       DESCRIPTION                = "description";
  String       DESTINATION                = "destination";
  String       DIRECTION                  = "direction";
  String       DISCARD                    = "discard";
  String       END                        = "end";
  String       ENVIRONMENT                = "environment";
  String       ENVIRONMENT_VARS           = "environment_vars";
  String       ERROR                      = "error";
  String       ERROR_COUNT                = "error-count";
  String       EXECUTABLE                 = "executable";
  String       EXECUTE_COMMAND            = "execute_command";
  String       EXECUTION_DURATION         = "execution-duration";
  String       FILE                       = "file";
  String       FILES                      = "files";
  String       FILES_COUNT                = "files-count";
  String       GAV_KEY                    = "Maven-Coordinates";
  String       GENERATED                  = "generated";
  int          HEAD                       = 0;
  String       HINT_MAP                   = "hint-map";
  String       ID                         = "id";
  String       ID_MAP_KEY                 = "id-map";
  String       ID_STRING                  = "id-string";
  String       IMAGE                      = "image";
  String       INDEX                      = "index";
  String       INLINE                     = "inline";
  String       INLINE_SHEBANG             = "inline_shebang";
  int          INTERRUPTED_ERROR          = -999;
  String       JSON_EXTENSION             = "json";
  String       LAST_RUN_UUID              = "last_run_uuid";
  String       LOG_LINES                  = "log-lines";
  String       MACHINE_READABLE_PARAM     = "-machine-readable";
  String       MANIFEST                   = "manifest";
  String       MERGED_MANIFEST            = "merged-manifest";
  List<String> MR_PARAMS                  = asList(COLOR_FALSE);
  String       NAME                       = "name";
  String       NIL                        = "nil";
  String       NO_TIMEOUT_SPECIFIED       = "NO TIMEOUT SPECIFIED";
  String       ORIGINAL_AUTH_ID           = "original-auth-id";
  String       ORIGINAL_MANIFEST          = "original-manifest";
  String       ORIGINALSOURCE             = "original-source";
  String       OUTPUT                     = "output";
  String       OVERRIDES                  = "override";
  String       PACKER                     = "packer";
  String       PACKER_BUILD_NAME          = "PACKER_BUILD_NAME";
  String       PACKER_BUILDER_TYPE        = "PACKER_BUILDER_TYPE";
  String       PACKER_COMMA               = "%!(PACKER_COMMA)";
  String       PACKER_EXECUTABLE_CHECKSUM = "packer-executable-checksum";
  String       PACKER_EXECUTION           = "packer-execution";
  String       PACKER_HTTP_ADDR           = "PACKER_HTTP_ADDR";
  String       PACKER_RUN_UUID            = "packer_run_uuid";
  String       PATH                       = "path";
  String       POST_PROCESSOR             = "post-processor";
  String       PRERELEASE                 = "pre-release";
  String       PROVISIONER                = "provisioner";
  String       REPOSITORY                 = "repository";
  String       SCRIPTS                    = "scripts";
  String       SHELL                      = "shell";
  String       SHELL_LOCAL                = "shell-local";
  String       SOURCE                     = "source";
  String       SOURCE_CLASS               = "source-class";
  String       SSH_USERNAME               = "ssh_username";
  String       START_TIME                 = "start-time";
  String       STRING                     = "string";
  String       STRIP_PATH                 = "strip_path";
  String       TAG                        = "tag";
  String       TAGS                       = "tags";
  String       TARGET                     = "target";
  String       TEMPLATE_BUILDER           = "template-builder";
  String       TEMPLATE_PROVISIONER       = "template-provisioner";
  String       TEMPLATE_VARIABLE          = "template-variable";
  String       TIMED_OUT                  = "timed-out";
  int          TIMED_OUT_ERROR            = -998;
  Duration     TWENTY_SECONDS             = ofMillis(20000);
  String       TYPE                       = "type";
  String       UI                         = "ui";
  String       UNKNOWN                    = "UNKNOWN";
  String       VERSION                    = "version";
  String       VERSION_COMMIT             = "version-commit";
  String       VERSION_PARAM              = "-version";
  String       VERSION_PRELEASE           = "version-prelease";
}
