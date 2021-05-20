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

import static org.infrastructurebuilder.automation.IBRTypedExecution.EXECUTIONDATA;
import static org.infrastructurebuilder.automation.IBRTypedExecution.SPECIFICDATA;
import static org.infrastructurebuilder.automation.PackerIBRExecutionDataReader.PACKER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.infrastructurebuilder.automation.PackerIBRExecutionDataReader.PackerTypedExecution;
import org.infrastructurebuilder.automation.model.v1_0_0.DependentExecution;
import org.infrastructurebuilder.automation.model.v1_0_0.PackerSpecificExecution;
import org.infrastructurebuilder.util.core.GAV;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackerIBRExecutionDataReaderTest {

  private PackerIBRExecutionDataReader r;
  private Xpp3Dom                      dom;
  private Instant                      now;
  private DependentExecution de;

  @Before
  public void setUp() throws Exception {
    now = Instant.now();
    r = new PackerIBRExecutionDataReader();
    dom = IBRAutomationException.et.withReturningTranslation(
        () -> Xpp3DomBuilder.build(getClass().getResourceAsStream("/PackerIBRExecutionDataReaderTest.xml"), "UTF-8"));
    de = new DependentExecution();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testReadTypedExecutionXpp3Dom() {
    Optional<Supplier<PackerTypedExecution>> v = r.readTypedExecution(dom, de);
    assertTrue(v.isPresent());
    Supplier<PackerTypedExecution> ps = v.get();
    PackerTypedExecution p = ps.get();
    PackerSpecificExecution sed = (PackerSpecificExecution) p.getSpecificExecutionData();
    GAV gav = sed.getGav();
    assertEquals("G:A:1.0.0", gav.asMavenDependencyGet().get());
    assertNotNull(p);
  }

  @Test
  public void testGetComponentHint() {
    assertEquals(PACKER, r.getComponentHint());
  }

  @Test
  public void testRespondsTo() {
    Xpp3Dom d2 = new Xpp3Dom(EXECUTIONDATA);
    assertFalse(r.respondsTo(d2));
    d2.addChild(dom.getChild(SPECIFICDATA));
    assertTrue(r.respondsTo(d2));
  }

}
