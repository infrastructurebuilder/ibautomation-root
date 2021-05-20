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
package org.infrastructurebuilder.ibr.utils;

import static org.infrastructurebuilder.util.constants.IBConstants.JSON_TYPE;
import static org.junit.Assert.assertEquals;

import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractAutomationUtilsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private AutomationUtilsTesting au;

  @Before
  public void setUp() throws Exception {
    this.au = new AutomationUtilsTesting();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAbstractAutomationUtilsAbstractAutomationUtils() {
    AbstractAutomationUtils au2 = new AbstractAutomationUtils(this.au) {
      @Override
      public AutomationUtils configure(ConfigMapSupplier cms) {
        // TODO Auto-generated method stub
        return this;
      }
    };

    assertEquals(au2.getExtensionForType(JSON_TYPE), au.getExtensionForType(JSON_TYPE));

  }

}
