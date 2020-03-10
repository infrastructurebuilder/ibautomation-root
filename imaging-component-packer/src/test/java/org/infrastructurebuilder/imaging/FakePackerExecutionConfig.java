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

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.DefaultArchiverManager;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.imaging.maven.ImageBuildExecutionConfig;
import org.infrastructurebuilder.imaging.maven.PackerBean;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.PackerManifest;
import org.infrastructurebuilder.imaging.maven.Type;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.json.JSONObject;

public class FakePackerExecutionConfig implements ImageBuildExecutionConfig {

  private static final String FAKE = "FAKE";
  private final DefaultIBAuthentication a;
  private final Path actualProjectBuildDirectory;
  private final DefaultGAV artifact;
  private final List<DefaultIBAuthentication> authentications;
  private PackerBean data = new PackerBean();
  private final ContainerConfiguration dpcreq;
  private PackerImageBuilder image;
  private final ClassWorld kw;
  private final Path packer;
  private final JSONObject settings;
  private final Path targetDirectory;
  private final IBAuthConfigBean writer;

  public FakePackerExecutionConfig(final Path actualTarget, final Path targetDirectory, final Path temp,
      final Path packer, final ClassWorld kw, final ContainerConfiguration dpcreq)
      throws PlexusContainerException, ComponentLookupException {
    this.kw = kw;
    this.dpcreq = dpcreq;
    this.packer = packer;
    actualProjectBuildDirectory = actualTarget;
    artifact = new DefaultGAV("org.junit:junit:1.2.3:jar");
    this.targetDirectory = targetDirectory;
    a = new DefaultIBAuthentication();
    settings = new JSONObject().put("A", new JSONObject().put("username", "Auser").put("password", "Bpassword"));
    a.setServerId("A");
    a.setType("amazon-ebs");
    a.setTarget("X");
    authentications = Arrays.asList(a);
    writer = new IBAuthConfigBean();
    writer.setTempDirectory(this.targetDirectory.resolve(UUID.randomUUID().toString()).toFile());
    writer.setAuths(authentications);
  }

  @Override
  public Map<String, String> getAdditionalEnvironment() {
    return Collections.emptyMap();
  }

  @Override
  public PrintStream getAdditionalPrintStream() {
    return System.out;
  }

  @Override
  public List<IBArchive> getIBRArchives() {
    return Collections.emptyList();
  }

  @Override
  public String getIBRHandler() {
    return IBArchive.IBR;
  }

  @Override
  public Map<IBArchive, IBRInternalDependency> getIBRMapping() {
    return Collections.emptyMap();
  }

  @Override
  public ArchiverManager getArchiverManager() {
    return new DefaultArchiverManager();
  }

  @Override
  public IBAuthConfigBean getAuthFileWriter() {
    return writer;
  }

  @Override
  public List<DefaultIBAuthentication> getBaseAuthentications() {
    return Collections.emptyList();
  }

  @Override
  public String getClassifier() {
    return "classifier";
  }

  @Override
  public GAV getCoordinates() {
    return artifact;
  }

  @Override
  public String getDescription() {
    return "Description";
  }

  @Override
  public Charset getEncoding() {
    return StandardCharsets.UTF_8;
  }

  @Override
  public String getExcept() {

    return null;
  }

  @Override
  public String getFinalName() {
    return UUID.randomUUID().toString();
  }

  @Override
  public PackerImageBuilder getImage() {
    image = new PackerImageBuilder();
    final List<Type> map = new ArrayList<>();
    final Type t = new Type();
    t.setHint("fake");
    map.add(t);
    image.setTypes(map);
    return image;
  }

  @Override
  public String getName() {
    return "name";
  }

  @Override
  public String getOnly() {
    return null;
  }

  @Override
  public Path getOutputDirectory() {
    return getTmpDir();
  }

  @Override
  public Path getPackerExecutable() {
    return packer;
  }

  @Override
  public List<PackerManifest> getPackerManifests() {
    return Collections.emptyList();
  }

  @Override
  public PlexusContainer getPlexusContainer() {
    return PackerException.et.withReturningTranslation(() -> {
      return new DefaultPlexusContainer(dpcreq,
          new WireModule(new SpaceModule(new URLClassSpace(kw.getClassRealm(FAKE)))));
    });
  }

  @Override
  public File getProjectBuildDirectory() {
    return actualProjectBuildDirectory.toFile();
  }

  @Override
  public Properties getProperties() {
    return new Properties();
  }

  @Override
  public List<IBRInternalDependency> getRequirements() {
    return Collections.emptyList();
  }

  @Override
  public JSONObject getSettingsJSON() {
    return settings;
  }

  @Override
  public String getTimeout() {

    return null;
  }

  @Override
  public Path getTmpDir() {
    return targetDirectory.resolve(UUID.randomUUID().toString());
  }

  @Override
  public File getVarFile() {

    return null;
  }

  @Override
  public Map<String, String> getVars() {
    return Collections.emptyMap();
  }

  @Override
  public Path getWorkingDirectory() {
    return getTmpDir();
  }

  @Override
  public boolean isCleanupOnError() {

    return false;
  }

  @Override
  public boolean isCopyToOtherRegions() {
    return false;
  }

  @Override
  public boolean isForce() {

    return false;
  }

  @Override
  public boolean isOverwrite() {

    return false;
  }

  @Override
  public boolean isParallel() {
    return true;
  }

  @Override
  public boolean isSkip() {
    return false;
  }

  @Override
  public boolean isSkipActualPackerRun() {
    return true;
  }

  @Override
  public boolean isSkipIfEmpty() {
    return true;
  }

  @Override
  public boolean isUnpackByDefault() {
    return true;
  }

  public void setData(final PackerBean data) {
    this.data = data;
  }

  public PackerBean getData() {
    return data;
  }
}
