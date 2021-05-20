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

import static java.lang.Class.forName;
import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.ORIGINAL_AUTH_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE_CLASS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TARGET;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;
import static org.infrastructurebuilder.util.core.IBUtils.getOptString;

import java.util.Optional;

import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONObject;

public class PackerHintMapDAO implements IBRHintMap {
  private final Optional<String> authId;
  private final Optional<String> hint;
  private final String           id;
  private final Class<?>         klass;
  private final Optional<String> target;
  private final Optional<String> type;

  public PackerHintMapDAO(final JSONObject j) {
    id = requireNonNull(j).getString(ID);
    authId = getOptString(j, ORIGINAL_AUTH_ID);
    type = getOptString(requireNonNull(j), TYPE);
    target = getOptString(j, TARGET);
    hint = getOptString(j, SOURCE);
    klass = et.withReturningTranslation(() -> forName(j.getString(SOURCE_CLASS)));
  }

  public PackerHintMapDAO(final Optional<IBAuthentication> a, final ImageBaseObject b) {
    id = b.getId();
    authId = requireNonNull(a).map(IBAuthentication::getId);
    type = requireNonNull(a).map(IBAuthentication::getType);
    target = a.flatMap(IBAuthentication::getTarget);
    hint = requireNonNull(b).getLookupHint();
    klass = b.getLookupClass();
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(ID, getId())

        .addString(ORIGINAL_AUTH_ID, getAuthId())

        .addString(TYPE, getType())

        .addString(TARGET, getTarget())

        .addString(SOURCE, getHint())

        .addString(SOURCE_CLASS, getKlass().getCanonicalName())

        .asJSON();
  }

  @Override
  public Optional<String> getAuthId() {
    return authId;
  }

  @Override
  public Optional<String> getHint() {
    return hint;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Class<?> getKlass() {
    return klass;
  }

  @Override
  public Optional<String> getTarget() {
    return target;
  }

  @Override
  public Optional<String> getType() {
    return type;
  }

}
