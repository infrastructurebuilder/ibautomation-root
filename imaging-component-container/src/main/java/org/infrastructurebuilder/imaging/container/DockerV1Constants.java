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
package org.infrastructurebuilder.imaging.container;

import static java.util.Arrays.asList;

import java.util.List;

public interface DockerV1Constants {
  String       AUTHOR                       = "author";
  String       CHANGES                      = "changes";
  String       CONTAINER_PATH               = "container_path";
  String       CONTAINER                    = "docker";
  String       DOCKER_IMPORT                = "docker-import";
  String       DOCKER_POST_PROCESSOR_IMPORT = "packer.post-processor.docker-import";
  String       DOCKER_PUSH                  = "docker-push";
  String       DOCKER_PUSH_PROCESSOR        = "packer.post-processor.docker-push";
  String       DOCKER_SAVE                  = "docker-save";
  String       DOCKER_SAVE_PROCESSOR        = "packer.post-processor.docker-save";
  String       DOCKER_TAG                   = "docker-tag";
  String       DOCKER_TAG_PROCESSOR         = "packer.post-processor.docker-tag";
  String       EXPORT_PATH                  = "export_path";
  String       HOST_PATH                    = "host_path";
  String       IMAGE                        = "image";
  String       LOGIN                        = "login";
  String       LOGIN_PASSWORD               = "login_password";
  String       LOGIN_SERVER                 = "login_server";
  String       LOGIN_USERNAME               = "login_username";
  String       MESSAGE                      = "message";
  String       PRIVILEGED                   = "privileged";
  String       PULL                         = "pull";
  String       REGION                       = "region";
  String       RUN_COMMAND                  = "run_command";
  List<String> VALID_CHANGES                = asList("CMD", "ENTRYPOINT", "ENV", "EXPOSE", "LABEL", "ONBUILD", "USER",
      "VOLUME", "WORKDIR");

}
