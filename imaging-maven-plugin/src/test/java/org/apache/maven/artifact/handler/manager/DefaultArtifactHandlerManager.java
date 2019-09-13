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
package org.apache.maven.artifact.handler.manager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;

@Named
public class DefaultArtifactHandlerManager implements ArtifactHandlerManager {

  @Inject
  private Map<String, ArtifactHandler> artifactHandlers;

  private final Map<String, ArtifactHandler> unmanagedHandlers = new ConcurrentHashMap<>();

  @Override
  public void addHandlers(final Map<String, ArtifactHandler> handlers) {

    unmanagedHandlers.putAll(handlers);
  }

  @Override
  public ArtifactHandler getArtifactHandler(final String type) {
    ArtifactHandler handler = unmanagedHandlers.get(type);

    if (handler == null) {
      handler = artifactHandlers.get(type);

      if (handler == null) {
        handler = new DefaultArtifactHandler(type);
      }
    }

    return handler;
  }

  @Deprecated
  public Set<String> getHandlerTypes() {
    return artifactHandlers.keySet();
  }

}
