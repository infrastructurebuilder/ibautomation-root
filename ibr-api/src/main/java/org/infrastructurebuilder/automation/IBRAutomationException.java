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

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;

public class IBRAutomationException extends RuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = -2262435310679542313L;
  public static ExceptionTranslator et = ET.newConfiguration().translate(Exception.class).to(IBRAutomationException.class)
      .done();
  public IBRAutomationException() {
    super();
  }
//  public IBRAutomationException(String message, Throwable cause, boolean enableSuppression,
//      boolean writableStackTrace) {
//    super(message, cause, enableSuppression, writableStackTrace);
//  }
  public IBRAutomationException(String message, Throwable cause) {
    super(message, cause);
  }
  public IBRAutomationException(String message) {
    super(message);
  }
//  public IBRAutomationException(Throwable cause) {
//    super(cause);
//  }

}
