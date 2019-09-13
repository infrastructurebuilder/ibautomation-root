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

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.auth.IBAuthentication;

public class FakePackerAuthentication implements IBAuthentication {

  private final Path target;
  private final String id;

  public FakePackerAuthentication(final Path target) {
    this.target = target;
    id = UUID.randomUUID().toString();
  }

  @Override
  public Optional<String> getAdditional() {
    return Optional.empty();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getMapKey() {

    return null;
  }

  @Override
  public Optional<String> getPrincipal() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getSecret() {
    return Optional.empty();
  }

  @Override
  public String getServerId() {

    return null;
  }

  @Override
  public Optional<String> getTarget() {
    return Optional.ofNullable(target.toString());
  }

  @Override
  public String getType() {
    return "fake";
  }

}
