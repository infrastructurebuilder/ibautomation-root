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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.infrastructurebuilder.configuration.management.IBRBuilderConfigElement;
import org.infrastructurebuilder.configuration.management.IBRDataObject;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.json.JSONObject;

@Mojo(name = "ibr-compile", requiresProject = true, threadSafe = false, requiresDependencyResolution = ResolutionScope.COMPILE, instantiationStrategy = InstantiationStrategy.PER_LOOKUP, defaultPhase = LifecyclePhase.COMPILE)
public class IBRCompileMojo extends AbstractIBRMojo<JSONObject> {

  @Override
  @SuppressWarnings("unchecked")
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info(String.format("Builders collected: %d", getBuilders().size()));
    if (getBuilders().size() == 0)
      throw new MojoExecutionException("No builders set");
    final List<String> order = (List<String>) Optional.ofNullable(getPluginContext().get(COMPILE_ORDER))
        .orElse(new ArrayList<>());
    getLog().info(String.format("Order set: %s", order));
    final Map<String, IBRDataObject<JSONObject>> map = (Map<String, IBRDataObject<JSONObject>>) Optional
        .ofNullable(getPluginContext().get(COMPILE_ITEMS)).orElse(new HashMap<>());
    getLog().info(String.format("Compile-time String-IBRDataObject map set: %s", map));
    for (final IBRBuilderConfigElement builder : getBuilders()) {
      final String type = builder.getType();
      getLog().info(String.format("Performing compile phase type check on %s", type));
      final IBRType<JSONObject> tt = Optional.ofNullable(getMyTypes().get(type))
          .orElseThrow(() -> new MojoExecutionException("No such type: " + type));
      tt.setConfigSupplier(builder);
      copyCMTypeSourcesAndResourcesToTarget(tt).ifPresent(path -> {
        map.put(builder.getId(), new IBRDataObject<JSONObject>(tt, path, builder));
        order.add(builder.getId());
      });
      getLog().info(String.format("Final compile order: %s", order));
      getLog().info(String.format("Final compile items: %s", map));
      getPluginContext().put(COMPILE_ORDER, order);
      getPluginContext().put(COMPILE_ITEMS, map);
    }
  }

}
