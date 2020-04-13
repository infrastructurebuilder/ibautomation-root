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
//
//import static java.util.Objects.requireNonNull;
//import static org.infrastructurebuilder.automation.IBRManifest.MODEL_VERSION;
//import static org.infrastructurebuilder.util.artifacts.Checksum.fromUTF8StringBytes;
//import static org.infrastructurebuilder.util.artifacts.GAV.GAV_ARTIFACTID;
//import static org.infrastructurebuilder.util.artifacts.GAV.GAV_CLASSIFIER;
//import static org.infrastructurebuilder.util.artifacts.GAV.GAV_EXTENSION;
//import static org.infrastructurebuilder.util.artifacts.GAV.GAV_GROUPID;
//import static org.infrastructurebuilder.util.artifacts.GAV.GAV_VERSION;
//
//import org.codehaus.plexus.util.xml.Xpp3Dom;
//import org.infrastructurebuilder.util.artifacts.Checksum;
//import org.infrastructurebuilder.util.artifacts.GAV;
//import org.infrastructurebuilder.util.artifacts.IBVersion;
//import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
//import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
//
///**
// * EncapsulatedExecutionData supplies a required format for the execution data
// * sets, as well as being extensible if some other form is needed.
// *
// * @author mykel.alvis
// *
// */
//public class DefaultSpecificExecution implements IBRSpecificExecution {
//
//  private final Xpp3Dom   dom;
//  private final GAV       signature;
//  private final String    type;
//  private final Checksum  checksum;
//  private final IBVersion modelVersion;
//
//  public DefaultSpecificExecution(Xpp3Dom d) {
//    this.dom = requireNonNull(d);
//    this.type = d.getAttribute(TYPE);
//    this.modelVersion = new DefaultIBVersion(d.getAttribute(MODEL_VERSION));
//    this.signature = new DefaultGAV(
//
//        d.getAttribute(GAV_GROUPID)
//
//        , d.getAttribute(GAV_ARTIFACTID)
//
//        , d.getAttribute(GAV_CLASSIFIER)
//
//        , d.getAttribute(GAV_VERSION)
//
//        , d.getAttribute(GAV_EXTENSION));
//    this.checksum = fromUTF8StringBytes(d.toString());
//  }
//
//  @Override
//  public Xpp3Dom getSpecificExecutionAsDom() {
//    return dom;
//  }
//
//  @Override
//  public GAV getGav() {
//    return signature;
//  }
//
//  @Override
//  public String getType() {
//    return type;
//  }
//
//  @Override
//  final public String toString() {
//    return getSpecificExecutionAsDom().toString();
//  }
//
//  @Override
//  public Checksum asChecksum() {
//    return this.checksum;
//  }
//
//  @Override
//  public String getModelVersion() {
//    return this.modelVersion.toString();
//  }
//
//}
