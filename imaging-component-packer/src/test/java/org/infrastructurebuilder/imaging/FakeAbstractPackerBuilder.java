/*
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.json.JSONObject;

@Named(FakeAbstractPackerBuilder.FAKE)
@Typed(ImageData.class)
public class FakeAbstractPackerBuilder extends AbstractPackerBuilder {

  public class FakeBuildResult extends AbstractPackerBuildResult implements ImageBuildResult {
    public FakeBuildResult(final JSONObject j) {
      super(j);
    }

  }

  public static final String FAKE = "fake";

  public FakeAbstractPackerBuilder() {
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject();
  }

  @Override
  public Optional<String> getAuthType() {
    return empty();
  }

  @Override
  public Optional<String> getLookupHint() {
    return of(FakeAbstractPackerBuilder.FAKE);
  }

  @Override
  public List<String> getNamedTypes() {
    return emptyList();
  }

  @Override
  public String getPackerType() {
    return "fake";
  }

  @Override
  public List<String> getSizes() {
    return asList(PackerSizing2.values()).stream().map(PackerSizing2::name).collect(toList());
  }

  @Override
  public ImageBuildResult mapBuildResult(final JSONObject a) {
    return new FakeBuildResult(a);
  }

}
