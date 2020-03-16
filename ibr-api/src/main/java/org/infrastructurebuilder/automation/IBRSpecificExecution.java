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
package org.infrastructurebuilder.automation;

import static java.util.Optional.empty;
import static org.codehaus.plexus.util.xml.Xpp3DomBuilder.build;
import static org.infrastructurebuilder.automation.IBRAutomationException.et;
import static org.infrastructurebuilder.util.IBUtils.stringFromDOM;
import static org.infrastructurebuilder.util.artifacts.Checksum.fromUTF8StringBytes;

import java.io.StringReader;
import java.util.Optional;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.Xpp3OutputEnabled;

public interface IBRSpecificExecution extends ChecksumEnabled, Xpp3OutputEnabled {
  public static final String TYPE = "type";

  Xpp3Dom getSpecificExecutionAsDom();

  GAV getGav();

  String getType();

  String getModelVersion();

  default Optional<IBRDependentExecution> getParent() {
    return empty();
  }

  default Checksum asChecksum() {
    return fromUTF8StringBytes(getSpecificExecutionAsDom().toString());
  }

  @Override
  default Xpp3Dom asXpp3Dom() {
    Xpp3Dom root = new Xpp3Dom(getType());
    root.addChild(et.withReturningTranslation(() -> build(new StringReader(stringFromDOM(getGav().asDom())))));
    Xpp3Dom d = new Xpp3Dom(IBRManifest.MODEL_VERSION);
    d.setValue(getModelVersion());
    root.addChild(d);
    return root;
  }

}