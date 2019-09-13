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
package org.infrastructurebuilder.maven.ibr;

import static org.infrastructurebuilder.configuration.management.IBArchive.IBR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRRootPathSupplier;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.util.IBUtils;

abstract public class AbstractIBRMojo<T> extends AbstractMojo {
  public static final String COMPILE_ITEMS = "_compile_MAP_Items";
  public static final String COMPILE_ORDER = "_compile_MAP_Order";

  protected static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

  protected static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };
  @Parameter(readonly = true)
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

  @Component(hint = IBR)
  private ZipArchiver archiver;

  @Component
  private ArchiverManager archiverManager;

  @Component(hint = IBR)
  private ArtifactHandler artifactHandler;

  @Component
  private ArtifactHandlerManager artifactHandlerManager;

  @Parameter(required = true)
  private List<DefaultIBRBuilderConfigElement> builders = new ArrayList<>();
  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File classesDirectory;

  @Parameter(property = "classifier")
  private String classifier;

  @Parameter
  private String[] excludes = null;

  @Parameter(property = "ibr.finalName", required = true, defaultValue = "${project.build.finalName}")
  private String finalName;

  @Parameter(property = "ibr.force", required = false)
  private boolean force = false;

  @Parameter
  private String[] includes = null;

  @Component(role = Archiver.class, hint = "jar")
  private JarArchiver jarArchiver;

  @Component
  private MavenProjectHelper mavenProjectHelper;

  @Parameter(defaultValue = "${mojoExecution}", readonly = true)
  private MojoExecution mojo;

  @Component(role = IBRType.class)
  private Map<String, IBRType<T>> myTypes = new HashMap<>();

  @Parameter(defaultValue = "${project.build.directory}", readonly = true)
  private File outputDirectory;

  @Parameter(property = "project", readonly = true, required = true)
  private MavenProject project;

  @Component(hint = "default")
  private IBRRootPathSupplier rootPathSupplier;

  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  @Parameter(required = true, defaultValue = "${basedir}/src/main")
  private File sources;

  @Parameter(required = true, defaultValue = "${basedir}/src/main/resources")
  private File unfilteredResourceDirectory;

  @Parameter(defaultValue = "${project.build.directory}/ibr-tmp", readonly = true)
  private File workDirectory;

  public MavenArchiveConfiguration getArchive() {
    return archive;
  }

  public ZipArchiver getArchiver() {
    return archiver;
  }

  public ArchiverManager getArchiverManager() {
    return archiverManager;
  }

  public ArtifactHandler getArtifactHandler() {
    return artifactHandler;
  }

  public ArtifactHandlerManager getArtifactHandlerManager() {
    return artifactHandlerManager;
  }

  public List<DefaultIBRBuilderConfigElement> getBuilders() {
    return builders;
  }

  public Path getClassesDirectory() {
    return classesDirectory.toPath();
  }

  public String getClassifier() {
    return classifier;
  }

  public String[] getExcludes() {
    if (excludes == null)
      excludes = new String[0];

    return excludes;
  }

  public String getFinalName() {
    return finalName;
  }

  public String[] getIncludes() {
    if (includes == null)
      includes = new String[0];
    return includes;
  }

  public MavenProjectHelper getMavenProjectHelper() {
    return mavenProjectHelper;
  }

  public MojoExecution getMojo() {
    return mojo;
  }

  public Map<String, IBRType<T>> getMyTypes() {
    rootPathSupplier.setPath(getWorkDirectory());
    return myTypes;
  }

  public Path getOutputDirectory() {
    return outputDirectory.toPath();
  }

  public MavenSession getSession() {
    return session;
  }

  public Path getSources() {
    return sources.toPath();
  }

  public Path getUnfilteredResourceDirectory() {
    return unfilteredResourceDirectory.toPath();
  }

  public Path getWorkDirectory() {
    return workDirectory.toPath().toAbsolutePath();
  }

  public boolean isForce() {
    return force;
  }

  public void setArchive(final MavenArchiveConfiguration archive) {
    this.archive = archive;
  }

  public void setArchiver(final ZipArchiver archiver) {
    this.archiver = archiver;
  }

  public void setArchiverManager(final ArchiverManager archiverManager) {
    this.archiverManager = archiverManager;
  }

  public void setArtifactHandler(final ArtifactHandler artifactHandler) {
    this.artifactHandler = artifactHandler;
  }

  public void setArtifactHandlerManager(final ArtifactHandlerManager artifactHandlerManager) {
    this.artifactHandlerManager = artifactHandlerManager;
  }

  public void setBuilders(final List<DefaultIBRBuilderConfigElement> builders) {
    this.builders = builders;
  }

  public void setClassesDirectory(final File classesDirectory) {
    this.classesDirectory = classesDirectory;
  }

  public void setClassifier(final String classifier) {
    this.classifier = classifier;
  }

  public void setExcludes(final String[] excludes) {
    this.excludes = excludes;
  }

  public void setExistentWorkDirectory(final File workDirectory) {
    this.workDirectory = workDirectory;
  }

  public void setFinalName(final String finalName) {
    this.finalName = finalName;
  }

  public void setForce(final boolean force) {
    this.force = force;
  }

  public void setIncludes(final String[] includes) {
    this.includes = includes;
  }

  public void setJarArchiver(final JarArchiver jarArchiver) {
    this.jarArchiver = jarArchiver;
  }

  public void setMavenProjectHelper(final MavenProjectHelper mavenProjectHelper) {
    this.mavenProjectHelper = mavenProjectHelper;
  }

  public void setMojo(final MojoExecution mojo) {
    this.mojo = mojo;
  }

  public void setMyTypes(final Map<String, IBRType<T>> myTypes) {
    this.myTypes = myTypes;
  }

  public void setOutputDirectory(final File outputDirectory) {
    this.outputDirectory = Objects.requireNonNull(outputDirectory).toPath().toAbsolutePath().toFile();
  }

  public void setProject(final MavenProject project) {
    this.project = project;
  }

  public void setRootPathSupplier(final IBRRootPathSupplier rootPathSupplier) {
    this.rootPathSupplier = rootPathSupplier;
  }

  public void setSession(final MavenSession session) {
    this.session = session;
  }

  public void setSources(final File sources) {
    this.sources = sources;
  }

  public void setUnfilteredResourcesDirectory(final File resources) {
    unfilteredResourceDirectory = resources;
  }

  public void setWorkDirectory(final File workDirectory) {
    this.workDirectory = workDirectory;
    IBArchiveException.et.withTranslation(() -> Files.createDirectories(getWorkDirectory()));
  }

  private int filesCount(final Path contentDirectory) {
    if (contentDirectory == null)
      return 0;
    if (!Files.isDirectory(contentDirectory))
      return 0;
    return IBArchiveException.et.withReturningTranslation(
        () -> Files.walk(contentDirectory).filter(Files::isRegularFile).collect(Collectors.toList()).size());
  }

  protected Optional<Path> copyCMTypeSourcesAndResourcesToTarget(final IBRType type) {
    final Path src = getSources().resolve(type.getArchiveSubPath());
    final Path dest = getWorkDirectory().resolve(type.getName());
    final Path resourceDestination = getWorkDirectory();
    final Path resources = getUnfilteredResourceDirectory();
    final List<Path> paths = IBArchiveException.et.withReturningTranslation(
        () -> Files.walk(src).filter(Files::isRegularFile).map(src::relativize).collect(Collectors.toList()));
    long count = paths.stream().map(file -> {
      final Path dFile = dest.resolve(file);
      if (!Files.exists(dFile.getParent())) {
        IBArchiveException.et.withTranslation(() -> Files.createDirectories(dFile.getParent()));
      }
      getLog().debug("Copying " + src.resolve(file) + " to " + dFile);
      IBArchiveException.et.withTranslation(() -> IBUtils.copy(src.resolve(file), dFile));
      return dFile;
    }).count();

    if (Files.exists(resources)) {
      final List<Path> resourcePaths = IBArchiveException.et.withReturningTranslation(() -> Files.walk(resources)
          .filter(Files::isRegularFile).map(resources::relativize).collect(Collectors.toList()));
      getLog().info(String.format("resourcePaths are %s, size is %d", resourcePaths, resourcePaths.size()));
      count += resourcePaths.stream().map(file -> {
        getLog().info(file.toString());
        final Path dFile = resourceDestination.resolve(file);
        if (!Files.exists(dFile.getParent())) {
          IBArchiveException.et.withTranslation(() -> Files.createDirectories(dFile.getParent()));
        }
        getLog().info("Copying " + resources.resolve(file) + " to " + dFile);
        IBArchiveException.et.withTranslation(() -> IBUtils.copy(resources.resolve(file), dFile));
        return dFile;
      }).count();
    } else {
      getLog().info("No resource directory existed");
    }

    return Optional.ofNullable(count > 0L ? dest : null);
  }

  protected File getJarFile(final File basedir, final String resultFinalName, final String classifier) {
    if (basedir == null)
      throw new IllegalArgumentException("basedir is not allowed to be null");
    if (resultFinalName == null)
      throw new IllegalArgumentException("finalName is not allowed to be null");

    final StringBuilder fileName = new StringBuilder(resultFinalName);

    if (getClassifier() != null) {
      fileName.append("-").append(classifier);
    }

    fileName.append(".ibr");

    return new File(basedir, fileName.toString());
  }

  protected MavenProject getProject() {
    return project;
  }

  File createArchive() throws MojoExecutionException {
    final File target = getJarFile(outputDirectory, getFinalName(), getClassifier());
    getLog().info("Archive file is " + target);
    archiver.setDestFile(target);
    archiver.setForced(isForce());

    try {
      final Path contentDirectory = getWorkDirectory();
      getLog().info("Content dir = " + contentDirectory);

      if (filesCount(contentDirectory) == 0) {
        getLog().warn("Archive will be empty - no content was marked for inclusion!");
      } else {
        archiver.addDirectory(contentDirectory.toFile(), getIncludes(), getExcludes());
      }

      archiver.createArchive();

      return target;
    } catch (final Exception e) {

      throw new MojoExecutionException("Error assembling archive", e);
    }
  }

}
