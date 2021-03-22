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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;

public class FakeIBRDialectSupplier extends AbstractIBRDialectSupplier {
  public static final String    TEST = "test";
  private final List<IBRInstanceType> lst;

  public FakeIBRDialectSupplier(AutomationUtils ibr) {
    super(ibr, TEST);
    lst = new ArrayList<>();
  }

  protected FakeIBRDialectSupplier(AutomationUtils ibr2, ConfigMapSupplier cms, int weight,
      List<IBRInstanceType> list) {
    super(ibr2, cms, TEST, weight);
    this.lst = list.stream().sorted().collect(toList());
  }

  @Override
  public IBRDialect get() {
    return new FakeDialect(getAutomationUtils(), this.lst).configure(getConfig().get());
  }

  @Override
  public IBRDialectSupplier configure(ConfigMapSupplier cms) {
    return new FakeIBRDialectSupplier(getAutomationUtils(), cms, getWeight(), this.lst);
  }

  public static class FakeDialect implements IBRDialect {
    private final AutomationUtils       ibr;
    private final List<IBRInstanceType> imageSizes;

    public FakeDialect(AutomationUtils ibr, List<IBRInstanceType> s) {
      this.ibr = requireNonNull(ibr);
      this.imageSizes = requireNonNull(s);
    }

    @Override
    public IBRDialect configure(ConfigMap c) {
      return new FakeDialect(this.ibr, this.imageSizes.stream().map(i -> i.configure(c)).collect(toList()));
    }

    @Override
    public String getDialect() {
      return TEST;
    }

    @Override
    public List<IBRInstanceType> getImageSizes() {
      return imageSizes.stream().collect(toList());
    }

    @Override
    public Optional<String> getImageSizeIdentifierFor(String size) {
      return this.imageSizes.stream().sorted().filter(f -> f.matches(size)).findFirst()
          .map(IBRInstanceType::getDialectSpecificIdentifier);
    }

  }

  public static class FakeIBRInstanceType extends AbstractStringBackedIBRInstanceType {

    public FakeIBRInstanceType(String tech, String id, int order, String realtype) {
      super(tech, id, realtype, order);
    }
  }

}
