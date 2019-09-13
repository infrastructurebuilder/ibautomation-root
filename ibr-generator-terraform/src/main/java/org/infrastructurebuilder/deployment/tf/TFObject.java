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
package org.infrastructurebuilder.deployment.tf;

import static java.util.Optional.*;

import java.util.Optional;

import static java.util.Objects.*;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.json.JSONObject;

abstract public class TFObject<T extends ChecksumEnabled> implements ChecksumEnabled {

  public final static BiFunction<String, Checksum, String> resourceNamed = (s, c) -> {
    return requireNonNull(s) + "_" + c.asUUID().toString().replace("-", "_");
  };
  public final static Function<String, String> nameGen = (str) -> {
    return UUID.nameUUIDFromBytes(requireNonNull(str).getBytes(IBUtils.UTF_8)).toString();
  };
  private final TFGenerator type;
  private final String name;
  private final T data;
  private final Optional<String> description;
  private final ChecksumBuilder b;

  public TFObject(final WorkingPathSupplier wps, final TFGenerator type, final String name,
      final Optional<String> description, final T data) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.data = requireNonNull(data);
    this.description = requireNonNull(description);
    // We build the root ChecksumBuilder and supply it to the underlying instances
    this.b = ChecksumBuilder.newInstance(of(wps.getRoot()))
        // type
        .addString(this.type.toString())
        // name
        .addString(this.name)
        // desc
        .addString(this.description)
        // data
        .addChecksumEnabled(data);
  }

  public T getData() {
    return data;
  }

  public String getName() {
    return name;
  }

  public TFGenerator getType() {
    return type;
  }

  public Optional<String> getDescription() {
    return description;
  }

  protected ChecksumBuilder getCheckumBuilder() {
    return b;
  }

  @Override
  public Checksum asChecksum() {
    // We depend on the underlying implementations to use getChecksumBuilder() to identify their actual checksums
    return b.asChecksum();
  }

  public final String getInstanceName() {
    return resourceNamed.apply(getName(), asChecksum());
  }
}
