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
package org.infrastructurebuilder.maven.imaging;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class LogHelper implements Log {

  private final List<Throwable> errors;

  private final List<CharSequence> infoMessages;

  private final List<CharSequence> warnMessages;

  public LogHelper() {
    warnMessages = new ArrayList<>();
    infoMessages = new ArrayList<>();
    errors = new ArrayList<>();
  }

  @Override
  public void debug(final CharSequence charSequence) {

  }

  @Override
  public void debug(final CharSequence charSequence, final Throwable throwable) {

  }

  @Override
  public void debug(final Throwable throwable) {

  }

  @Override
  public void error(final CharSequence charSequence) {

  }

  @Override
  public void error(final CharSequence charSequence, final Throwable throwable) {
    errors.add(throwable);
  }

  @Override
  public void error(final Throwable throwable) {

  }

  public List<Throwable> getErrors() {
    return errors;
  }

  public List<CharSequence> getInfoMessages() {
    return infoMessages;
  }

  public List<CharSequence> getWarnMessages() {
    return warnMessages;
  }

  @Override
  public void info(final CharSequence charSequence) {
    infoMessages.add(charSequence);
  }

  @Override
  public void info(final CharSequence charSequence, final Throwable throwable) {

  }

  @Override
  public void info(final Throwable throwable) {

  }

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public boolean isInfoEnabled() {
    return true;
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public void warn(final CharSequence charSequence) {
    warnMessages.add(charSequence);
  }

  @Override
  public void warn(final CharSequence charSequence, final Throwable throwable) {

  }

  @Override
  public void warn(final Throwable throwable) {

  }
}
