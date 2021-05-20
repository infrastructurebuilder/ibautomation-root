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
package org.infrastructurebuilder.configuration.management;

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;

public class IBArchiveException extends RuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = 9159647344533049812L;
  public static ExceptionTranslator et = ET.newConfiguration().translate(Exception.class).to(IBArchiveException.class)
      .done();

  public IBArchiveException(final String message) {
    super(message);
  }

  public IBArchiveException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public IBArchiveException(final Throwable cause) {
    super(cause);
  }
}
