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

import static org.infrastructurebuilder.IBConstants.CHECKSUM_TYPES_DEFAULT;
import static org.infrastructurebuilder.IBConstants.CHECKSUM_TYPES_SHA512;
import static org.infrastructurebuilder.IBConstants.SHA512;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.CHECKSUM;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.CHECKSUM_TYPES;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.OUTPUT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

@Named("checksum")
@Typed(PackerPostProcessor.class)
@Description("Checksum Post Processor")
public class PackerChecksumPostProcessor extends AbstractPackerPostProcessor {

  private final String fileName = UUID.randomUUID().toString();

  public PackerChecksumPostProcessor() {
    super();
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(TYPE, getPackerType())

        .addJSONArray(CHECKSUM_TYPES, CHECKSUM_TYPES_DEFAULT)

        .addPath(OUTPUT, getOutputPath(CHECKSUM_TYPES_SHA512))

        .asJSON();
  }

  public String getFileName() {
    return fileName;
  }

  @Override
  public Optional<Map<String, Path>> getForcedOutput() {
    final Map<String, Path> value = new HashMap<>();
    value.put(CHECKSUM, getOutputPath().get());
    return Optional.of(value);
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.empty();
  }

  @Override
  public List<String> getNamedTypes() {
    return Collections.emptyList();
  }

  @Override
  public Optional<Path> getOutputPath() {
    return super.getOutputPath(Optional.of(SHA512));
  }

  @Override
  public String getPackerType() {
    return CHECKSUM;
  }

  @Override
  public void validate() {
    if (!this.getOutputPath().isPresent())
      throw new PackerException("No target directory");
  }

}
