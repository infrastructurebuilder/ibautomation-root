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
package org.infrastructurebuilder.imaging.shell;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.ENVIRONMENT_VARS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.EXECUTE_COMMAND;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.INLINE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.INLINE_SHEBANG;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_BUILDER_TYPE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_BUILD_NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_HTTP_ADDR;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SCRIPTS;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.SHELL;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.TYPE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.AbstractPackerPostProcessor;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

public class PackerShellPostProcessor extends AbstractPackerPostProcessor {
  
  static final List<String> namedTypes = new ArrayList<String>() {
    /**
     *
     */
    private static final long serialVersionUID = 5439676293769919204L;

    /**
     *
     */

    {
      add(SHELL);
    }
  };

  private Map<String, String> envs;
  private String executeCommand;
  private List<String> inlines;
  private String inlineShebang;
  private List<Path> scripts;

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(TYPE, getPackerType())

        .addListString(SCRIPTS, getScripts())

        .addListString(INLINE, getInlines())

        .addListString(ENVIRONMENT_VARS, getEnvs())

        .addString(INLINE_SHEBANG, getInlineShebang())

        .addString(EXECUTE_COMMAND, getExecuteCommand())

        .asJSON();
  }

  public Optional<String> getExecuteCommand() {
    return Optional.ofNullable(executeCommand);
  }

  public Optional<List<String>> getInlines() {
    return Optional.ofNullable(inlines);
  }

  public Optional<String> getInlineShebang() {
    return Optional.ofNullable(inlineShebang);
  }

  @Override
  public Optional<String> getLookupHint() {
    return Optional.of(SHELL);
  }

  @Override
  public List<String> getNamedTypes() {
    return namedTypes;
  }

  @Override
  public String getPackerType() {
    return SHELL;
  }

  public Optional<List<String>> getScripts() {
    return Optional.ofNullable(scripts)
        .map(s -> s.stream().map(Path::toAbsolutePath).map(Path::toString).collect(Collectors.toList()));
  }

  public void setEnvs(final Map<String, String> envs) {
    this.envs = Objects.requireNonNull(envs);
  }

  public void setExecuteCommand(final String executeCommand) {
    this.executeCommand = Objects.requireNonNull(executeCommand);
  }

  public void setInlines(final List<String> inlines) {
    this.inlines = Objects.requireNonNull(inlines);
  }

  public void setInlineShebang(final String inlineShebang) {
    this.inlineShebang = Objects.requireNonNull(inlineShebang);
  }

  public void setScripts(final List<Path> scripts) {
    this.scripts = Objects.requireNonNull(scripts);
  }

  @Override
  public void validate() {
    if (getScripts().isPresent() && getInlines().isPresent())
      throw new PackerException("Cannot set scripts and inlines simultaneously");
    if (!(getScripts().isPresent() || getInlines().isPresent()))
      throw new PackerException("Must have scripts or inlines");
    Optional.ofNullable(envs).ifPresent(env -> {
      final Set<String> keys = env.keySet();
      for (final String s : Arrays.asList(PACKER_BUILD_NAME, PACKER_BUILDER_TYPE, PACKER_HTTP_ADDR))
        if (keys.contains(s))
          throw new PackerException("You cannot include an environment value for " + s);
    });
  }

  Optional<List<String>> getEnvs() {
    final Optional<List<String>> x = Optional.ofNullable(envs)
        .map(e -> e.entrySet().stream().map(ee -> ee.getKey() + "=" + ee.getValue()).collect(Collectors.toList()));
    return x.flatMap(x1 -> Optional.ofNullable(x1.size() == 0 ? null : x1));
  }

}
