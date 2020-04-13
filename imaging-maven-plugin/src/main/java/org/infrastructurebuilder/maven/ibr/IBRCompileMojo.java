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
package org.infrastructurebuilder.maven.ibr;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.infrastructurebuilder.configuration.management.IBRBuilderConfigElement;
import org.infrastructurebuilder.configuration.management.IBRDataObject;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.json.JSONObject;

@Mojo(name = "ibr-compile", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE, instantiationStrategy = InstantiationStrategy.PER_LOOKUP, defaultPhase = LifecyclePhase.COMPILE)
public class IBRCompileMojo extends AbstractIBRMojo {

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info(format("Builders collected: %d", getBuilders().size()));
    if (getBuilders().size() == 0)
      throw new MojoExecutionException("No builders set");
    final List<String> order = getCompileOrder().orElse(new ArrayList<>());
    getLog().info(format("Order set: %s", order));
    final Map<String, IBRDataObject<JSONObject>> map = getCompileItems().orElse(new HashMap<>());
    getLog().info(format("Compile-time String-IBRDataObject map set: %s", map));
    for (final IBRBuilderConfigElement builder : getBuilders()) {
      final String type = builder.getType();
      getLog().info(format("Performing compile phase type check on %s", type));
      final IBRType tt = ofNullable(getMyTypes().get(type))
          .orElseThrow(() -> new MojoExecutionException("No such type: " + type));
      tt.setConfigSupplier(builder);
      copyCMTypeSourcesAndResourcesToTarget(tt).ifPresent(path -> {
        map.put(builder.getId(), new IBRDataObject<JSONObject>(tt, path, builder));
        order.add(builder.getId());
      });
      setCompileOrder(order);
      setCompileItems(map);
    }
  }

}
