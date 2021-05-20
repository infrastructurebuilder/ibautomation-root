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
package org.infrastructurebuilder.automation;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.automation.IBRAutomationException.et;
import static org.infrastructurebuilder.util.core.DefaultIBVersion.DefaultIBVersionBoundedRange.versionBoundedRangeFrom;
import static org.infrastructurebuilder.util.core.IBUtils.removeXMLPrefix;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.infrastructurebuilder.automation.PackerIBRExecutionDataReader.PackerTypedExecution;
import org.infrastructurebuilder.automation.model.v1_0_0.PackerSpecificExecution;
import org.infrastructurebuilder.util.core.Checksum;

@Named(PackerIBRExecutionDataReader.PACKER)
@Singleton
public class PackerIBRExecutionDataReader extends AbstractIBRExecutionDataReader<PackerTypedExecution> {

  private final static org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.PackerManifestXpp3Reader v1_0_0reader = new org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.PackerManifestXpp3Reader();
  private final static org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.PackerManifestXpp3Writer v1_0_0writer = new org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.PackerManifestXpp3Writer();

  public static final String                                                                              PACKER       = "packer";

  private static final String                                                                             V1_0_0_LOWER = "1.0.0";                                                                                 // FIXME
                                                                                                                                                                                                                  // These
                                                                                                                                                                                                                  // need
                                                                                                                                                                                                                  // to
                                                                                                                                                                                                                  // be
                                                                                                                                                                                                                  // replaced
                                                                                                                                                                                                                  // with
                                                                                                                                                                                                                  // the
                                                                                                                                                                                                                  // local
                                                                                                                                                                                                                  // model
                                                                                                                                                                                                                  // version
  private static final String                                                                             V1_0_0_UPPER = "1.0.0";

  @Inject
  public PackerIBRExecutionDataReader() {
    super(PACKER, PACKER, versionBoundedRangeFrom(V1_0_0_LOWER, V1_0_0_UPPER));
  }

  public Optional<Supplier<PackerTypedExecution>> readTypedExecution(Xpp3Dom ed, IBRDependentExecution parent) {
    return ofNullable(ed).map(xpp3Dom -> {
      return () -> new PackerTypedExecution(ed, parent);
    });
  };

  static class PackerTypedExecution extends AbstractIBRExecutionData<PackerSpecificExecution> {

    private final PackerSpecificExecution val;

    public PackerTypedExecution(Xpp3Dom d, IBRDependentExecution parent) {
      super(d, parent);
      this.val = et.withReturningTranslation(
          () -> v1_0_0reader.read(new StringReader(removeXMLPrefix(getSpecificData().toUnescapedString()))));
    }

    public PackerTypedExecution(String executable, Instant start, Instant end, Checksum check, String originalSource,
        PackerSpecificExecution specific, IBRDependentExecution parent) {
      super(executable, start, end, check, originalSource, requireNonNull(specific).asXpp3Dom(), parent);
      this.val = specific;
    }

    @Override
    public IBRTypedExecution copy() {
      return new PackerTypedExecution(super.asXpp3Dom(), getParent());
    }

    @Override
    public IBRSpecificExecution getSpecificExecutionData() {
      return val;
    }

    @Override
    protected Xpp3Dom constructSpecificData() {
      StringWriter sw = new StringWriter();
      et.withTranslation(() -> v1_0_0writer.write(sw, val));
      return et.withReturningTranslation(() -> Xpp3DomBuilder.build(new StringReader(sw.toString())));
    }

    @Override
    public Optional<IBRImageMap> getImageMap() {
      return ofNullable(val).map(v -> new DefaultIBRImageMap(v.getImages().stream().collect(toList())));
    }

  }

}
