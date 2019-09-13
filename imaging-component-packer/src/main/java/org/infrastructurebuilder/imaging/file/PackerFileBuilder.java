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
package org.infrastructurebuilder.imaging.file;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.CONTENT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SOURCE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TARGET;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.imaging.AbstractPackerBuildResult;
import org.infrastructurebuilder.imaging.AbstractPackerBuilder;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.PackerSizing;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

@Typed(ImageData.class)
@Named(PackerFileBuilder.FILETYPE)
@Description("Generic EBS-backed AWS instance")
public class PackerFileBuilder extends AbstractPackerBuilder<JSONObject> {
  public class PackerFileBuildResult extends AbstractPackerBuildResult {
    public PackerFileBuildResult(final JSONObject j) {
      super(j);
    }
  }

  public static final String FILETYPE = "file";
  static final List<String> namedTypes = new ArrayList<String>() {
    private static final long serialVersionUID = -5931265214102237440L;
    {
      add(FILETYPE);
    }
  };

  @Override
  public JSONObject asJSON() {
    final JSONObject j = JSONBuilder.newInstance().addString(TYPE, getPackerType())
        .addString(NAME, getBuildExecutionName()).addPath(TARGET, getOutputPath()).addPath(SOURCE, getUserDataFile())
        .addString(CONTENT, getContent()).asJSON();
    return j;
  }

  @Override
  public Optional<String> getAuthType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(FILETYPE);
  }

  @Override
  public List<String> getNamedTypes() {
    return namedTypes;
  }

  @Override
  public String getPackerType() {
    return FILETYPE;
  }

  @Override
  public List<PackerSizing> getSizes() {
    return Arrays.asList(PackerSizing.small);
  }

  @Override
  public ImageBuildResult mapBuildResult(final JSONObject a) {
    return new PackerFileBuildResult(a);
  }

  @Override
  public void validate() {
    if (!getOutputPath().isPresent())
      throw new PackerException("No target output for file");
    if (!(getUserDataFile().isPresent() || getContent().isPresent()))
      throw new PackerException("No file contents provided");
    super.validate();
  }

}
