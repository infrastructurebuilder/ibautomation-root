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

import static org.junit.Assert.assertEquals;

import org.infrastructurebuilder.imaging.PackerException;
import org.junit.Test;

public class TestExceptions {

  public static final String BUILD = "build";
  public static final String COULD_NOT_LOCATE_SUBTYPE_FROM_UNKNOWN = "Could not locate subtype from unknown";
  public static final String COULD_NOT_LOCATE_TYPE_FROM_UNKNOWN = "Could not locate type from unknown";
  public static final String DOES_NOT_EXIST = "doesNotExist";
  public static final String ERROR_MESSAGE = "ErrorMessage";
  public static final int MILLIS_1 = 1;
  public static final int MINUTES_30 = 30;
  public static final String SRC_TEST_RESOURCES_PACKER = "src/test/resources/packer";
  public static final String SRC_TEST_RESOURCES_TEST_JSON_JSON = "src/test/resources/testJSON.json";
  public static final String UNKNOWN = "unknown";

  @Test
  public void testPackerException() {
    PackerException pe = new PackerException(ERROR_MESSAGE);
    assertEquals(ERROR_MESSAGE, pe.getMessage());
    final Throwable cause = new Throwable(ERROR_MESSAGE);
    pe = new PackerException(cause);
    assertEquals(cause.toString(), pe.getMessage());
    pe = new PackerException(ERROR_MESSAGE, cause);
    assertEquals(ERROR_MESSAGE, pe.getMessage());
    assertEquals(cause, pe.getCause());
  }

  @Test
  public void testPackerInterruptedException() {
    PackerInterruptedException pie = new PackerInterruptedException(ERROR_MESSAGE);
    assertEquals(ERROR_MESSAGE, pie.getMessage());
    final Throwable cause = new Throwable(ERROR_MESSAGE);
    pie = new PackerInterruptedException(cause);
    assertEquals(cause.toString(), pie.getMessage());
    pie = new PackerInterruptedException(ERROR_MESSAGE, cause);
    assertEquals(ERROR_MESSAGE, pie.getMessage());
    assertEquals(cause, pie.getCause());
  }

  @Test
  public void testPackerTimeoutException() {
    PackerException pte = new PackerTimeoutException(ERROR_MESSAGE);
    assertEquals(ERROR_MESSAGE, pte.getMessage());
    final Throwable cause = new Throwable(ERROR_MESSAGE);
    pte = new PackerTimeoutException(cause);
    assertEquals(cause.toString(), pte.getMessage());
    pte = new PackerTimeoutException(ERROR_MESSAGE, cause);
    assertEquals(ERROR_MESSAGE, pte.getMessage());
    assertEquals(cause, pte.getCause());
  }

}
