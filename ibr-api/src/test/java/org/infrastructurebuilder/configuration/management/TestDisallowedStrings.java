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
package org.infrastructurebuilder.configuration.management;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestDisallowedStrings {
  public static final String GOOD_STRING = "This String is so good\r\nYou wouldn't believe it\r\nAstounding.";
  public static final String BAD_STRING = "This String contains disallowed items.\r\nThey're kind of buried${executionId}, so you need to be pretty good to spot them\r\nAdditional words";
  public static final String EMPTY_STRING = "";

  public static final MockDisallowedStringsImpl disallowedStringImpl = new MockDisallowedStringsImpl();

  @Test
  public void testBadString() {
    assertFalse("BAD_STRING fails validation", disallowedStringImpl.isValid(BAD_STRING));
  }

  @Test
  public void testEmptyString() {
    assertTrue("EMPTY_STRING passes validation", disallowedStringImpl.isValid(EMPTY_STRING));
  }

  @Test
  public void testGoodString() {
    assertTrue("GOOD_STRING passes validation", disallowedStringImpl.isValid(GOOD_STRING));
  }
}
