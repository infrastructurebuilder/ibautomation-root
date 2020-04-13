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
package org.infrastructurebuilder.imaging;

import static org.infrastructurebuilder.imaging.FakeIBRDialectSupplier.TEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.automation.IBRAutomationException;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.DefaultConfigMapSupplier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IBRDialectMapperTest {
  private static final String          MEDIOCRE = "mediocre";
  private static final String          LARGE    = "large";
  private static final String          MEDIUM   = "medium";
  private static final String          SMALL    = "small";
  private final static AutomationUtils ibr      = new AutomationUtilsTesting();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  protected List<IBRDialectSupplier> ts;
  protected IBRDialectSupplier       t1, t2, t3;
  protected IBRDialectMapper         m;
  protected ConfigMapSupplier        cms1, cms2;
  protected List<IBRInstanceType>    l1, l2;
  protected IBRInstanceType          type1, type2, type3, type4, type5;

  @Before
  public void setUp() throws Exception {
    type1 = new FakeIBRDialectSupplier.FakeIBRInstanceType(TEST, SMALL, 1, SMALL);
    type2 = new FakeIBRDialectSupplier.FakeIBRInstanceType(TEST, MEDIUM, 2, MEDIUM);
    type3 = new FakeIBRDialectSupplier.FakeIBRInstanceType(TEST, LARGE, 3, LARGE);
    type4 = new FakeIBRDialectSupplier.FakeIBRInstanceType(TEST, MEDIUM, 4, MEDIOCRE);
    type5 = new FakeIBRDialectSupplier.FakeIBRInstanceType("NOT TEST", MEDIUM, 4, MEDIOCRE);
    l1 = Arrays.asList(type1);
    l2 = Arrays.asList(type1, type2, type3, type4, type5);
    cms1 = new DefaultConfigMapSupplier();
    cms2 = new DefaultConfigMapSupplier();
    t1 = new FakeIBRDialectSupplier(ibr, cms1, 0, l1);
    t2 = new FakeIBRDialectSupplier(ibr, cms2, 2, l2);
    ts = Arrays.asList(t1, t2);
    m = new IBRDialectMapper(ibr, ts);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test(expected = IBRAutomationException.class)
  public void testUnConfigured() {
    t3 = new FakeIBRDialectSupplier(ibr);
    t3.get();

  }

  @Test
  public void test() {
    String type = "test";
    Optional<ConfigMapSupplier> cms = Optional.empty();
    Optional<IBRDialect> t = m.getDialectFor(type, cms);
    assertTrue(t.isPresent());
    IBRDialect tx = t.get();
    IBRInstanceType l = tx.getImageSizes().iterator().next();
    assertFalse(l.getMillicentsPerMinute().isPresent());
    assertFalse(l.respondsToDialect("BOB"));
    assertEquals(MEDIOCRE, tx.getImageSizeIdentifierFor(MEDIUM).get());
    assertEquals(LARGE, tx.getImageSizeIdentifierFor(LARGE).get());
    assertFalse(tx.getImageSizeIdentifierFor("BOB").isPresent());
  }

  private void assertFalse(boolean respondsToTechnology) {
    // TODO Auto-generated method stub

  }

}
