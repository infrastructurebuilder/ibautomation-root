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

import static org.infrastructurebuilder.imaging.PackerCompressType.zip;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.COMPRESS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.COMPRESSION_LEVEL;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.OUTPUT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

@Named(COMPRESS)
@Description("Compression post processor")
@Typed(PackerPostProcessor.class)
public class PackerCompressPostProcessor extends AbstractPackerPostProcessor {

  private int compressionLevel = 6;

  private PackerCompressType compressionType = zip;

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(TYPE, getPackerType())

        .addInteger(COMPRESSION_LEVEL, getCompressionLevel())

        .addPath(OUTPUT, getOutputPath(Optional.of(getCompressionType().getTypeString())))

        .asJSON();
  }

  public int getCompressionLevel() {
    return compressionLevel;
  }

  public PackerCompressType getCompressionType() {
    return compressionType;
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(COMPRESS);
  }

  @Override
  public List<String> getNamedTypes() {
    return Collections.emptyList();
  }

  @Override
  public String getPackerType() {
    return COMPRESS;
  }

  public void setCompressionLevel(final int l) {
    if (l <= 9 && l >= 1) {
      compressionLevel = l;
    }
  }

  public void setCompressionType(final PackerCompressType compressionType) {
    this.compressionType = Objects.requireNonNull(compressionType);
  }

  @Override
  public void validate() {
    if (!this.getOutputPath().isPresent())
      throw new PackerException("No target directory");
  }

}
