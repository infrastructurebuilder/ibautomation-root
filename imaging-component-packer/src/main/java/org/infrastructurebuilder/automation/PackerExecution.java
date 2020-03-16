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

//import org.codehaus.plexus.util.xml.Xpp3Dom;
//
//public class PackerExecution extends AbstractIBRExecutionData {
//
//  public PackerExecution(Xpp3Dom d) {
//    super(d);
//  }
//
//  @Override
//  public IBRExecutionData copy() {
//    return new PackerExecution(asXpp3Dom());
//  }
//
//  @Override
//  public DefaultSpecificExecution getSpecificExecutionData() {
//    return new PackerSpecificExecutionData(getSpecificData(), previousKeys);
//  }
//
//  @Override
//  protected Xpp3Dom constructSpecificData() {
//    Xpp3Dom p = new Xpp3Dom(PackerIBRExecutionDataReader.PACKER);
//    Xpp3Dom env = new Xpp3Dom(ENVIRONMENT);
//    Xpp3Dom executable = new Xpp3Dom(EXECUTABLE);
//    Xpp3Dom execChecksum = new Xpp3Dom(CHECKSUM);
//    Xpp3Dom execVer = new Xpp3Dom(VERSION);
//    Xpp3Dom data = new Xpp3Dom(DATA);
//    p.addChild(executable);
//    p.addChild(execVer);
//    p.addChild(execChecksum);
//    p.addChild(env);
//    p.addChild(data);
//    return p;
//  }
//
//}
