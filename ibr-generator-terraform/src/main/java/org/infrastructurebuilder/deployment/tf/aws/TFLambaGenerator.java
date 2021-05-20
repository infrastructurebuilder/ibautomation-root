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
package org.infrastructurebuilder.deployment.tf.aws;

import static java.util.Objects.requireNonNull;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.deployment.tf.AbstractTFGenerator;
import org.infrastructurebuilder.deployment.tf.TFGenerator;
import org.infrastructurebuilder.deployment.tf.TFModel;
import org.infrastructurebuilder.deployment.tf.TFObject;

@Named("aws_lambda_function")
@Typed(TFGenerator.class)
public class TFLambaGenerator extends AbstractTFGenerator {

  public final TFModel model;

  @Inject
  public TFLambaGenerator(TFModel model) {
    this.model = requireNonNull(model);
  }

  @Override
  public TFObject<?> generate(TFModel resource) {
    return null;
  }

}
