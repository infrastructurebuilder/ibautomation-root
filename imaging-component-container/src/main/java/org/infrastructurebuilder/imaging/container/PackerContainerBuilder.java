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
package org.infrastructurebuilder.imaging.container;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.COMMIT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.DEFAULT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.DISCARD;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;
import static org.infrastructurebuilder.imaging.container.DockerCommitType.commit;
import static org.infrastructurebuilder.imaging.container.DockerCommitType.export_path;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.AUTHOR;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.CHANGES;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.CONTAINER;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.EXPORT_PATH;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.IMAGE;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.MESSAGE;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.PRIVILEGED;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.PULL;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.RUN_COMMAND;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.VALID_CHANGES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.AbstractPackerBuildResult;
import org.infrastructurebuilder.imaging.AbstractPackerBuilder;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerSizing2;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

@Named(CONTAINER)
@Typed(ImageData.class)
public class PackerContainerBuilder extends AbstractPackerBuilder {
  public class PackerDockerBuildResult extends AbstractPackerBuildResult {

    public PackerDockerBuildResult(final JSONObject j) {
      super(j);
    }
  }

  private static final List<String> SIZES = Arrays.asList(PackerSizing2.small.name());

  @SuppressWarnings("serial")
  static final List<String> namedTypes = new ArrayList<String>() {
    {
      add(CONTAINER);
    }
  };

  private String           author;
  private List<String>     changes;
  private DockerCommitType commitType = commit;
  private boolean          privileged;

  private List<String> runCommands;

  @Override
  public void addRequiredItemsToFactory(final IBAuthentication a, final PackerFactory f) {

    setInstanceAuthentication(a);

    f.addUniqueBuilder(
        (left, right) -> right.getLookupHint().orElse(DEFAULT).compareTo(left.getLookupHint().orElse(DEFAULT)), this);
  }

  @Override
  public JSONObject asJSON() {
    final JSONBuilder jb = JSONBuilder.newInstance().addString(TYPE, getPackerType())

        .addString(IMAGE, getSourceImage())

        .addString(AUTHOR, getAuthor())

        .addString(MESSAGE, getDescription())

        .addBoolean(PRIVILEGED, isPrivileged())

        .addBoolean(PULL, true)

        .addJSONArray(CHANGES, getChanges().map(JSONArray::new))

        .addJSONArray(RUN_COMMAND, getRunCommands().map(JSONArray::new));
    switch (getCommitType()) {
      case commit:
        jb.addBoolean(COMMIT, true);
        break;
      case discard:
        jb.addBoolean(DISCARD, true);
        break;
      case export_path:
        jb.addPath(EXPORT_PATH, getTargetOutput());
        break;
    }
    return jb.asJSON();
  }

  public Optional<String> getAuthor() {
    return ofNullable(author);
  }

  @Override
  public Optional<String> getAuthType() {
    return of(CONTAINER);
  }

  public Optional<List<String>> getChanges() {
    return ofNullable(changes);
  }

  public DockerCommitType getCommitType() {
    return commitType;
  }

  @Override
  public Optional<String> getLookupHint() {
    return of(CONTAINER);
  }

  @Override
  public List<String> getNamedTypes() {
    return namedTypes;
  }

  @Override
  public String getPackerType() {
    return CONTAINER;
  }

  public Optional<List<String>> getRunCommands() {
    return ofNullable(runCommands);
  }

  @Override
  public List<String> getSizes() {
    return SIZES;
  }

  public Optional<Map<String, String>> getVolumes() {
    return getDisk().map(d -> d.stream().map(x -> (PackerDockerBuilderDisk) x)
        .collect(Collectors.toMap(k -> k.getHostPath().get(), v -> v.getContainerPath().get())));
  }

  public boolean isPrivileged() {
    return privileged;
  }

  @Override
  public ImageBuildResult mapBuildResult(final JSONObject a) {
    return new PackerDockerBuildResult(a);
  }

  public void setAuthor(final String author) {
    this.author = requireNonNull(author);
  }

  public void setChanges(final List<String> changes) {
    this.changes = requireNonNull(changes);
    getChanges().map(c -> {
      final List<String> y = c.stream().map(s -> s.toUpperCase().split(" ")[0]).filter(v -> !VALID_CHANGES.contains(v))
          .collect(toList());
      return y;
    }).ifPresent(x -> {
      if (x.size() > 0)
        throw new PackerException("Invalid Changes specified " + x);
    });
  }

  public void setCommitType(final DockerCommitType commitType) {
    this.commitType = requireNonNull(commitType);
  }

  public void setPrivileged(final boolean privileged) {
    this.privileged = privileged;
  }

  public void setRunCommands(final List<String> runCommands) {
    this.runCommands = requireNonNull(runCommands);
  }

  @Override
  public void validate() {
    switch (getCommitType()) {
      case export_path:
        if (!getTargetOutput().isPresent())
          throw new PackerException("There is no output directory for " + export_path);
        break;
      default:
        break;
    }
  }

}
