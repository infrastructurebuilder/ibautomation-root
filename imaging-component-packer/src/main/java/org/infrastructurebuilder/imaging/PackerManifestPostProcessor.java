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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.MANIFEST;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.OUTPUT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.STRIP_PATH;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONObject;

@Named(PackerManifestPostProcessor.PACKER_MANIFEST)
@Typed(PackerPostProcessor.class)
@Description("Post processor to obtain a manifest")
public class PackerManifestPostProcessor extends AbstractPackerPostProcessor {
  public static final String PACKER_MANIFEST = "packer-manifest";
  private static final String MANIFEST_FILENAME = PackerManifestPostProcessor.PACKER_MANIFEST;

  public PackerManifestPostProcessor() {
    super.setOutputFileName(MANIFEST_FILENAME);
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance().addString(TYPE, getPackerType())

        .addPath(OUTPUT, getOutputPath())

        .addBoolean(STRIP_PATH, false)

        .asJSON();
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
  public String getPackerType() {
    return MANIFEST;
  }

  @Override
  public void setOutputFileName(final String outputFileName) {

  }

  @Override
  public void validate() {
    if (!this.getOutputPath().isPresent())
      throw new PackerException("No target directory");
  }
}
