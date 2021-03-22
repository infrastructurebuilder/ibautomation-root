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

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.isDirectory;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.infrastructurebuilder.configuration.management.IBArchiveException.et;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;


public abstract class AbstractIBRType implements IBRType {
  private Optional<IBConfigSupplier> config;

  private String                id = UUID.randomUUID().toString();
  private String                name;
  private final AutomationUtils rcs;

  private final List<IBRValidator> validators;

  public AbstractIBRType(final AutomationUtils rps, final List<IBRValidator> validators) {
    rcs = requireNonNull(rps);
    this.validators = requireNonNull(validators);
  }

  @Override
  public SortedSet<IBRValidationOutput> collectValidatedOutput() {
    final SortedSet<IBRValidationOutput> o = new TreeSet<>();
    final String subPath = (String) requireNonNull(getConfig().get().get("file"));

    for (final IBRValidator v : getRelevantValidators()) {
      o.addAll(v.validate(getRoot().resolve(Paths.get(subPath))));
    }
    return o;
  }

  @Override
  public AutomationUtils getAutomationUtils() {
    return rcs;
  }

  @Override
  public Optional<Map<String, Object>> getConfig() {
    return config.map(k -> k.get());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<IBRValidator> getRelevantValidators() {
    return getValidators().stream().filter(v -> v.respondsTo(this)).collect(toSet());
  }

  public Path getRoot() {
    final Path root = rcs.getWorkingPath();
    if (!isDirectory(root)) {
      et.withTranslation(() -> createDirectories(root));
    }
    return root;
  }

  public final List<IBRValidator> getValidators() {
    return validators;
  }

  @Override
  public void setConfigSupplier(final IBConfigSupplier acs) {
    config = Optional.of(acs);
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

}
