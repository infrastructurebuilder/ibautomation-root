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
package org.infrastructurebuilder.imaging.aws.ami.sizes;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.imaging.IBRDialect;
import org.infrastructurebuilder.imaging.IBRDialectSupplier;
import org.infrastructurebuilder.imaging.aws.ami.IBRAWSAMISupplier;
import org.infrastructurebuilder.util.config.DefaultConfigMapSupplier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractAWSIBRInstanceTypeTest {

  private static final String JEFF = "jeff";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private List<AbstractAWSIBRInstanceType> l;
  private AbstractAWSIBRInstanceType       gpu, gpuLarge, large, medium, micro, reallyBig, small, stupid;
  private AutomationUtils                  ibr;
  private IBRAWSAMISupplier                s;
  private DefaultConfigMapSupplier         cms;

  @Before
  public void setUp() throws Exception {
    ibr = new AutomationUtilsTesting();
    gpu = new AWSGpu();
    gpuLarge = new AWSGpuLarge();
    large = new AWSLarge();
    medium = new AWSMedium();
    micro = new AWSMicro();
    reallyBig = new AWSReallyBig();
    small = new AWSSmall();
    stupid = new AWSStupid() {
      @Override
      public List<String> getAliases() {
        return Arrays.asList(JEFF);
      }
    };
    l = Arrays.asList(gpu, gpuLarge, large, medium, micro, reallyBig, small, stupid);
    s = new IBRAWSAMISupplier(ibr, l.stream().collect(toList()));
    cms = new DefaultConfigMapSupplier();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetImageSizes() {
    assertEquals(8, s.configure(cms).get().getImageSizes().size());
  }

  @Test
  public void testConfigure() {
    IBRDialectSupplier x = s.configure(cms);
    assertNotNull(x.getAutomationUtils());
    IBRDialect d = x.get();
    assertNotNull(d);
    assertEquals(IBRAWSAMISupplier.TYPE, d.getDialect());
  }

  @Test
  public void testGetDialectSpecificIdentifier() {
    assertEquals("t3.small", s.configure(cms).get().getImageSizeIdentifierFor("small").get());
    assertEquals("m5d.metal", s.configure(cms).get().getImageSizeIdentifierFor(JEFF).get());
    assertFalse(s.configure(cms).get().getImageSizeIdentifierFor("NOT JEFF!").isPresent());
  }

  @Test
  public void testGetSize() {
    l.forEach(l1 -> {
      assertNotNull(l1.getSize());
    });
  }

  @Test
  public void testGetDialect() {
    l.forEach(l1 -> {
      assertEquals(IBRAWSAMISupplier.TYPE, l1.getDialect());
    });
  }

  @Test
  public void testGetOrder() {
    assertEquals(100, large.getOrder());
  }

}
