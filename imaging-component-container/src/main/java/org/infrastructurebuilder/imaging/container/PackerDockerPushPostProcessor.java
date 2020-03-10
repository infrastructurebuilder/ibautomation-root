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
package org.infrastructurebuilder.imaging.container;

import static org.infrastructurebuilder.imaging.container.DockerV1Constants.DOCKER_PUSH;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.LOGIN;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.LOGIN_PASSWORD;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.LOGIN_SERVER;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.LOGIN_USERNAME;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerPostProcessor;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.json.JSONArray;
import org.json.JSONObject;

@Named(DOCKER_PUSH)
@Typed(PackerPostProcessor.class)
public class PackerDockerPushPostProcessor extends AbstractDockerPostProcessor {

  private String loginServer;

  public PackerDockerPushPostProcessor() {
    super(DOCKER_PUSH);
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject();
  }

  @Override
  public JSONArray asJSONArray() {
    return new JSONArray(Arrays
        .asList(
            getInstanceAuthentication().orElseThrow(() -> new PackerException("No auth provided for " + DOCKER_PUSH)))
        .stream().filter(a -> respondsTo(a.getType())).map(this::getPostProcessorForAuth).collect(Collectors.toList()));
  }

  @Override
  public boolean isMultiAuthCapable() {
    return true;
  }

  public void setLoginServer(final String loginServer) {
    this.loginServer = Objects.requireNonNull(loginServer);
  }

  @Override
  public void validate() {
    if (!getLoginServer().isPresent())
      throw new PackerException("No login server is present");
  }

  private JSONObject getPostProcessorForAuth(final IBAuthentication a) {
    return super.asJSONBuilder().addBoolean(LOGIN, true)

        .addString(LOGIN_USERNAME, a.getPrincipal())

        .addString(LOGIN_PASSWORD, a.getSecret())

        .addString(LOGIN_SERVER, a.getTarget())

        .asJSON();
  }

  Optional<String> getLoginServer() {
    return Optional.ofNullable(loginServer);
  }

}
