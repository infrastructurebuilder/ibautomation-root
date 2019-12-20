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
package org.infrastructurebuilder.deployment.tf.aws;

import java.util.Optional;

import org.infrastructurebuilder.deployment.tf.TFGenerator;
import org.infrastructurebuilder.deployment.tf.TFObject;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;

abstract class TFAWSObject<T extends ChecksumEnabled> extends TFObject<T> {

  public TFAWSObject(final WorkingPathSupplier wps, TFGenerator type, String name, Optional<String> description, T data) {
    super(wps, type, name, description, data);
  }

}