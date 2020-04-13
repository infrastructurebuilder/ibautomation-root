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
package org.infrastructurebuilder.imaging.ibr;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.imaging.AbstractPackerProvisioner;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.json.JSONObject;

public abstract class AbstractPackerIBRProvisioner extends AbstractPackerProvisioner implements PackerIBRProvisioner {

  @Named(GenericProvisioner.GENERIC_IBR_PROVISIONER)
  @Typed(PackerProvisioner.class)
  @Description("Generic IBR Provisioner add-on")
  public static class GenericProvisioner extends AbstractPackerProvisioner {
    public static final String        GENERIC_IBR_PROVISIONER = "generic-ibr-provisioner";
    private final Optional<IBArchive> archive;
    private final IBRType             iBRType;
    private final String              lType;
    private final Path                path;

    public GenericProvisioner(final IBRType thisType, final String ltype, final Path p,
        final Optional<IBArchive> arch) {
      super();
      iBRType = requireNonNull(thisType);
      lType = requireNonNull(ltype);
      path = requireNonNull(p);
      archive = requireNonNull(arch);
    }

    @Override
    public JSONObject asJSON() {
      return iBRType.transformToProvisionerEntry(lType, getWorkingRootDirectory(), path, archive, getBuilders());
    }

    @Override
    public Optional<String> getLookupHint() {
      return of(GENERIC_IBR_PROVISIONER);
    }

    @Override
    public List<String> getNamedTypes() {
      return Collections.emptyList();
    }

    @Override
    public String getPackerType() {
      return null;
    }

    @Override
    public void validate() {
    }

  }

  private IBArchive                     archive;
  private final Map<String, IBRType> cmTypes;

  @Inject
  public AbstractPackerIBRProvisioner(final Map<String, IBRType> cmTypes) {
    super();
    this.cmTypes = requireNonNull(cmTypes);
  }

  @Override
  public List<PackerProvisioner> applyArchive(final IBArchive archive) {
    this.archive = requireNonNull(archive);
    final List<PackerProvisioner> list = new ArrayList<>();
    getArchive().ifPresent(a -> {
      final List<String> notfound = a.getPathKeys();
      notfound.removeAll(cmTypes.keySet());

      if (notfound.size() > 0)
        throw new IBArchiveException("The following types were not available as a IBRType : " + notfound);

      a.getPathList().forEach(pl -> {
        final Path p = getWorkingRootDirectory().resolve(pl.getPath());
        final IBRType type = cmTypes.get(pl.getKey());
        final GenericProvisioner gp = new GenericProvisioner(type, pl.getKey(), p, getArchive());
        gp.setBuilders(getBuilders());
        gp.setLog(getLog());
        list.add(gp);
      });
    });
    return list;
  }

  public Optional<IBArchive> getArchive() {
    return ofNullable(archive);
  }

  @Override
  public List<String> getNamedTypes() {
    return new ArrayList<>(cmTypes.values().stream().map(IBRType::getName).collect(toSet()));
  }

  @Override
  public void validate() {
    if (!getArchive().isPresent())
      throw new PackerException("No archive in " + getClass().getName());
    if (getNamedTypes().size() == 0)
      throw new PackerException("No named types were availalble in " + getClass().getName());
  }

}