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
package org.infrastructurebuilder.imaging.maven;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.ImageBuilder;
import org.infrastructurebuilder.imaging.ImageStorage;
import org.infrastructurebuilder.imaging.PackerSizing2;

public class PackerImageBuilder implements ImageBuilder {

  private final List<ImageStorage> disks               = new ArrayList<>();
  private final List<String>       postProcessingHints = new ArrayList<>();
  private String                   size                = PackerSizing2.small.name();
  private Map<String, String>      tags                = new HashMap<>();
  private final List<Type>         types               = new ArrayList<>();

  public PackerImageBuilder() {

  }

  @Override
  public List<ImageStorage> getDisks() {
    int i = 0;
    for (final ImageStorage ps : disks) {
      ps.withIndex(i++);
    }
    return disks;
  }

  @Override
  public List<String> getPostProcessingHints() {
    return postProcessingHints;
  }

  @Override
  public String getSize() {
    return size;
  }

  @Override
  public Map<String, String> getTags() {
    return tags;
  }

  @Override
  public Optional<Type> getTypeFor(final String hint) {
    return types.stream().filter(t -> requireNonNull(hint).equals(t.getHint())).findFirst();
  }

  @Override
  public List<String> getTypeHints() {
    return types.stream().map(Type::getHint).collect(toList());
  }

  @Override
  public List<Type> getTypes() {
    return types.stream().collect(toList());
  }

  public void setDisks(final List<ImageStorage> disks) {
    requireNonNull(disks).forEach(this::addDisk);
  }

  public void setPostProcessors(final List<String> postProcs) {
    requireNonNull(postProcs).forEach(this::addPostProcessingHint);
  }

  public void setSize(final String size) {
    this.size = size;
  }

  public void setTags(final Map<String, String> tags) {
    this.tags = tags;
  }

  public void setTypes(final List<Type> typeMap) {
    requireNonNull(typeMap).forEach(t -> {
      final String h = t.getHint();
      if (__thisTypes().containsKey(h))
        throw new PackerException("Duplicate hint " + h);
      types.add(t);
    });
  }

  private Map<String, Type> __thisTypes() {
    return types.stream().collect(toMap(k -> k.getHint(), identity()));
  }

  ImageBuilder addDisk(final ImageStorage disk) {
    disks.add(requireNonNull(disk));
    return this;
  }

  ImageBuilder addPostProcessingHint(final String hint) {

    if (postProcessingHints.contains(hint))
      throw new PackerException("Duplicate post processors are not allowed ' " + hint + "'");
    postProcessingHints.add(requireNonNull(hint));
    return this;
  }
}
