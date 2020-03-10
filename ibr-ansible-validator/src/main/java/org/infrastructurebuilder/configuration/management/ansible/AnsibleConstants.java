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
package org.infrastructurebuilder.configuration.management.ansible;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

public interface AnsibleConstants {
  String  ANSIBLE                         = "ansible";
  Pattern ANSIBLE_DRY_RUN_DISABLE_PATTERN = compile("check_mode\\s*:\\s*no");
  String  AWS_PROVISIONING_USER           = "user";

}
