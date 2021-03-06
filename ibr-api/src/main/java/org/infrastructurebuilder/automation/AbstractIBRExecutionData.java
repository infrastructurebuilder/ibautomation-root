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
import static org.infrastructurebuilder.automation.IBRManifest.dtf;
import static org.infrastructurebuilder.ibr.IBRConstants.BLNK;
import static org.infrastructurebuilder.ibr.IBRConstants.CDATA_END;
import static org.infrastructurebuilder.ibr.IBRConstants.CDATA_START;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.util.core.Checksum;

abstract public class AbstractIBRExecutionData<T> implements IBRTypedExecution {

  protected final static List<String> previousKeys = Arrays.asList(START, END, CHECKSUM, ORIGINAL_SOURCE);

  private final String                executable;
  private final Instant               start, end;
  private final Checksum              checksum;
  private final String                src;
  private final Xpp3Dom               original;
  private final Xpp3Dom               specificData;
  private final IBRDependentExecution parent;

  protected static Optional<String> opt(Xpp3Dom d, String key) {
    return Optional.ofNullable(Objects.requireNonNull(d).getChild(key)).map(n -> n.getValue());
  }

  protected static String str(Xpp3Dom d, String key) {
    return opt(d, key).orElseThrow(() -> new IBRAutomationException("Key " + key + " has no value in dom " + d));
  }

  protected static Instant date(Xpp3Dom d, String key) {
    return Instant.parse(str(d, key));
  }

  public AbstractIBRExecutionData(Xpp3Dom d, IBRDependentExecution parent) {
    this(str(d, EXECUTABLE), date(d, START), date(d, END), new Checksum(str(d, CHECKSUM)), str(d, ORIGINAL_SOURCE),
        requireNonNull(d.getChild(SPECIFICDATA)), parent);
  }

  public AbstractIBRExecutionData(String executable, Instant start, Instant end, Checksum check, String originalSource,
      Xpp3Dom specific, IBRDependentExecution parent) {
    Xpp3Dom d = constructExecutionData(executable, start, end, check, originalSource, specific);
    this.original = d;
    this.executable = str(d, EXECUTABLE);
    this.start = date(d, START);
    this.end = date(d, END);
    this.checksum = new Checksum(str(d, CHECKSUM));
    this.src = str(d, ORIGINAL_SOURCE);
    this.specificData = requireNonNull(d.getChild(SPECIFICDATA));
    this.parent = requireNonNull(parent);
  }

  private final static String cData(String src, boolean cData) {
    return new StringJoiner(BLNK, cData ? CDATA_START : BLNK, cData ? CDATA_END : BLNK).add(src).toString();

  }

  private final static Xpp3Dom child(String key, Object value, boolean cData) {
    Xpp3Dom c = new Xpp3Dom(key);
    if (requireNonNull(value) instanceof Xpp3Dom)
      cData = true;
    cData = false;
    c.setValue(cData(value.toString(), cData));
    return c;
  }

  public final static Xpp3Dom constructExecutionData(String executable, Instant start, Instant end, Checksum checksum,
      String src, Xpp3Dom executionData) {
    Xpp3Dom ret = new Xpp3Dom(EXECUTIONDATA);
    ret.addChild(child(EXECUTABLE, requireNonNull(executable), false));
    ret.addChild(child(START, dtf.format(requireNonNull(start)), false));
    ret.addChild(child(END, dtf.format(requireNonNull(end)), false));
    ret.addChild(child(CHECKSUM, requireNonNull(checksum), false));
    ret.addChild(child(ORIGINAL_SOURCE, requireNonNull(src), false));
    ret.addChild(requireNonNull(executionData));
    return ret;

  }

  @Override
  public IBRDependentExecution getParent() {
    return this.parent;
  }

  @Override
  public Instant getStart() {
    return start;
  }

  @Override
  public Instant getEnd() {
    return end;
  }

  @Override
  public String getExecutable() {
    return executable;
  }

  @Override
  public Checksum getExecutionChecksum() {
    return checksum;
  }

  @Override
  public String getSpecificExecutionSource() {
    return src;
  }

  protected Xpp3Dom getSpecificData() {
    return this.specificData;
  }

  public Xpp3Dom asXpp3Dom() {
    Xpp3Dom ret = new Xpp3Dom(EXECUTIONDATA);
    ret.addChild(child(EXECUTABLE, requireNonNull(executable), false));
    ret.addChild(child(START, requireNonNull(start), false));
    ret.addChild(child(END, requireNonNull(end), false));
    ret.addChild(child(CHECKSUM, requireNonNull(checksum), false));
    ret.addChild(child(SPECIFICDATA, requireNonNull(constructSpecificData()), true));
    return ret;
  }

  protected abstract Xpp3Dom constructSpecificData();
}
