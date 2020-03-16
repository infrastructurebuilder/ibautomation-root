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
//import static org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.DefaultIBVersionBoundedRange.versionBoundedRangeFrom;
//
//import java.io.StringReader;
//import java.util.Optional;
//import java.util.function.Supplier;
//
//import javax.inject.Inject;
//import javax.inject.Named;
//import javax.inject.Singleton;
//
//import org.codehaus.plexus.util.xml.Xpp3Dom;
//import org.infrastructurebuilder.automation.model.v1_0_0.PackerSpecificData;
//import static org.infrastructurebuilder.automation.IBRAutomationException.*;
//
//@Named(PackerIBRExecutionDataReader.PACKER)
//@Singleton
//public class PackerIBRExecutionDataReader extends AbstractIBRExecutionDataReader<PackerExecution> {
//
//  private final static org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.PackerManifestXpp3Reader v1_0_0reader = new org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.PackerManifestXpp3Reader();
//
//  static final String PACKER = "packer";
//
//  private static final String THIS_VERSION = "1.0.0";
//
//  @Inject
//  public PackerIBRExecutionDataReader() {
//    super(PACKER, versionBoundedRangeFrom(THIS_VERSION, THIS_VERSION));
//  }
//
//  @Override
//  public Optional<Supplier<PackerExecution>> readExecutionData(Xpp3Dom ed) {
//    PackerSpecificData x = et.withReturningTranslation(() -> v1_0_0reader.read(new StringReader(ed.toString())));
//
//    return Optional.empty();
//  }
//
//}
