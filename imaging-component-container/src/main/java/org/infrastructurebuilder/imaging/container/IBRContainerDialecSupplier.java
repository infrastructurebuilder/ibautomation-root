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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.imaging.AbstractIBRDialectSupplier;
import org.infrastructurebuilder.imaging.IBRDialect;
import org.infrastructurebuilder.imaging.IBRDialectSupplier;
import org.infrastructurebuilder.imaging.IBRInstanceType;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;

@Named(IBRContainerDialecSupplier.TYPE)
@Singleton
public class IBRContainerDialecSupplier extends AbstractIBRDialectSupplier {

  public static final String TYPE = "container";

  private final List<IBRInstanceType> imageSizes;

  @Inject
  public IBRContainerDialecSupplier(AutomationUtils ibr, List<IBRInstanceType> sizings) {
    super(ibr, TYPE);
    this.imageSizes = requireNonNull(sizings).stream().filter(t -> t.respondsToDialect(TYPE)).collect(toList());
  }

  private IBRContainerDialecSupplier(AutomationUtils ibr, ConfigMapSupplier cms, List<IBRInstanceType> isizes,
      int weight) {
    super(ibr, cms, TYPE, weight);
    this.imageSizes = requireNonNull(isizes);
  }

  @Override
  public String getDialect() {
    return TYPE;
  }

  @Override
  public IBRDialectSupplier configure(ConfigMapSupplier cms) {
    return new IBRContainerDialecSupplier(getAutomationUtils(), cms, this.imageSizes, getWeight());
  }

  @Override
  public IBRDialect get() {
    return new ContainerDialect(getAutomationUtils(), this.imageSizes).configure(getConfig().get());
  }

  private final class ContainerDialect implements IBRDialect {

    private final IBRuntimeUtils        ibr;
    private final List<IBRInstanceType> imageSizes;

    public ContainerDialect(IBRuntimeUtils ibr, List<IBRInstanceType> s) {
      this.ibr = requireNonNull(ibr);
      this.imageSizes = requireNonNull(s);
    }

    @Override
    public IBRDialect configure(ConfigMap c) {
      return new ContainerDialect(this.ibr, this.imageSizes.stream().map(i -> i.configure(c)).collect(toList()));
    }

    @Override
    public String getDialect() {
      return TYPE;
    }

    @Override
    public List<IBRInstanceType> getImageSizes() {
      return imageSizes.stream().collect(toList());
    }

    @Override
    public Optional<String> getImageSizeIdentifierFor(String size) {
      return this.imageSizes.stream().filter(f -> f.matches(size)).findFirst()
          .map(IBRInstanceType::getDialectSpecificIdentifier);
    }

  }

}
