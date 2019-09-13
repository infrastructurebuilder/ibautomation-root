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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.config.PathSupplier;

public abstract class AbstractIBRType<T> implements IBRType<T> {
  private Optional<IBConfigSupplier> config;

  private String id = UUID.randomUUID().toString();
  private String name;
  private final PathSupplier rcs;

  private final List<IBRValidator> validators;

  public AbstractIBRType(final PathSupplier rps, final List<IBRValidator> validators) {
    rcs = Objects.requireNonNull(rps);
    this.validators = Objects.requireNonNull(validators);
  }

  @Override
  public SortedSet<IBRValidationOutput> collectValidatedOutput() {
    final SortedSet<IBRValidationOutput> o = new TreeSet<>();
    final String subPath = (String) Objects.requireNonNull(getConfig().get().get("file"));

    for (final IBRValidator v : getRelevantValidators()) {
      o.addAll(v.validate(getRoot().resolve(Paths.get(subPath))));
    }
    return o;
  }

  @Override
  public Path getArchiveSubPath() {
    return Paths.get(getName());
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
    return getValidators().stream().filter(v -> v.respondsTo(this)).collect(Collectors.toSet());
  }

  public Path getRoot() {
    final Path root = rcs.get();
    if (!Files.isDirectory(root)) {
      IBArchiveException.et.withTranslation(() -> Files.createDirectories(root));
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
