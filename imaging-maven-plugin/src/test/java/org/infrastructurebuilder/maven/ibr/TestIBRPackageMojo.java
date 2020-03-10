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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.handler.manager.DefaultArtifactHandlerManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DefaultMavenProjectHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.infrastructurebuilder.configuration.management.DefaultIBConfigSupplier;
import org.infrastructurebuilder.configuration.management.DummyIBRType;
import org.infrastructurebuilder.configuration.management.IBRDataObject;
import org.infrastructurebuilder.configuration.management.IBRType;
import org.infrastructurebuilder.configuration.management.ansible.AnsibleIBRType;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleIBRValidator;
import org.infrastructurebuilder.configuration.management.ansible.DefaultAnsibleValidator;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.ibr.utils.IBRWorkingPathSupplier;
import org.infrastructurebuilder.maven.imaging.FakeArchiverManager;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestIBRPackageMojo {
  @Rule
  public ExpectedException               expected = ExpectedException.none();
  private DefaultIBRBuilderConfigElement builderEntry;

  private Path                      emptyWorkDirectory;
  private IBRPackageMojo            m;
  private Map<Object, Object>       mavenMap;
  private final TestingPathSupplier ps = new TestingPathSupplier();
  private AutomationUtils           rps;
  private File                      sneakyFile;

  private Path                target;
  private Path                test;
  private IBRType<JSONObject> testFakeCMType;
  private IBRType<JSONObject> testFakeNoFileCMType;

  private HashMap<String, IBRType<JSONObject>> typeMap;

  @After
  public void cleanup() {
    IBUtils.deletePath(test);
    IBUtils.deletePath(sneakyFile.toPath());
    if (Files.exists(Paths.get(target.toString(), "testFinalName.ibr"))) {
      IBUtils.deletePath(Paths.get(target.toString(), "testFinalName.ibr"));
    }
    IBUtils.deletePath(emptyWorkDirectory);
  }

  @Before
  public void setup() throws Exception {
    target = ps.getRoot();
    rps = new AutomationUtilsTesting();

    test = Paths.get(target.toString(), FakeDummyIBRType.FAKE_TYPE_FILE);
    sneakyFile = Paths.get(target.toString(), "sneakyFile").toFile();
    emptyWorkDirectory = Paths.get(target.toString(), "testEmptyWorkDirectory");
    Files.createDirectory(emptyWorkDirectory);
    Files.createFile(sneakyFile.toPath());
    Files.createFile(test);

    final Map<String, Object> hm = new HashMap<>();
    hm.put("file", "ABC");
    testFakeCMType = new DummyIBRType();
    testFakeCMType.setConfigSupplier(new DefaultIBConfigSupplier().setConfig(hm));
    testFakeNoFileCMType = new FakeDummyNoFileIBRType();
    testFakeNoFileCMType.setConfigSupplier(new DefaultIBConfigSupplier().setConfig(hm));
    typeMap = new HashMap<>();
    typeMap.put("fake", testFakeCMType);

    builderEntry = new DefaultIBRBuilderConfigElement();
    builderEntry.setType("fake");

    mavenMap = new HashMap<>();

    final Map<String, IBRDataObject<JSONObject>> itemMap = new HashMap<>();
    itemMap.put(FakeDummyIBRType.FAKE_TYPE_FILE, new IBRDataObject<>(testFakeCMType, Paths.get("."), builderEntry));

    mavenMap.put(AbstractIBRMojo.COMPILE_ORDER, asList(FakeDummyIBRType.FAKE_TYPE_FILE));
    mavenMap.put(AbstractIBRMojo.COMPILE_ITEMS, itemMap);

    final ArtifactHandler artifactHandler = new DefaultArtifactHandler("jar");
    final Artifact artifact = new DefaultArtifact("fake.company.group", "fake", "0.1.0-SNAPSHOT", "scope", "type",
        "classifier", artifactHandler);

    m = new IBRPackageMojo();
    m.setProject(new MavenProject());
    m.getProject().setArtifact(artifact);

    final ZipArchiver archiver = new ZipArchiver();
    m.setArchiver(archiver);
    m.setFinalName("testFinalName");
    m.setMyTypes(typeMap);
    m.setPluginContext(mavenMap);
    m.setOutputDirectory(target.toFile());
    m.setWorkDirectory(target.toFile());
    IBRWorkingPathSupplier i = new IBRWorkingPathSupplier();
    i.setT(rps.getWorkingPath());
    m.setRootPathSupplier(i);
    final IBRType<JSONObject> a = new AnsibleIBRType(rps,
        asList(new DefaultAnsibleIBRValidator(rps, new DefaultAnsibleValidator())));
    a.setConfigSupplier(new DefaultIBConfigSupplier().setConfig(new HashMap<>()));
    final Map<String, IBRType<JSONObject>> myTypes = asList(a).stream()
        .collect(Collectors.toMap(k -> a.getName(), Function.identity()));
    m.setMyTypes(myTypes);

  }

  @Test
  public void testIBRAbstractMojoPortions() {
    m.setArchive(new MavenArchiveConfiguration());
    m.setArchiverManager(new FakeArchiverManager());
    m.setArtifactHandler(new DefaultArtifactHandler());
    m.setArtifactHandlerManager(new DefaultArtifactHandlerManager());
    m.setClassesDirectory(target.toFile());
    m.setExcludes(new String[0]);
    m.setForce(false);
    m.setIncludes(new String[0]);
    m.setJarArchiver(new JarArchiver());
    m.setMavenProjectHelper(new DefaultMavenProjectHelper());
    m.setMojo(new MojoExecution(null));
    m.setSession(null);
    assertNotNull(m.getArchive());
    assertNotNull(m.getArchiverManager());
    assertNotNull(m.getArtifactHandler());
    assertNotNull(m.getArtifactHandlerManager());
    assertNotNull(m.getClassesDirectory());
    assertNotNull(m.getMojo());
    assertNull(m.getSession());
    assertNotNull(m.getArchiver());
  }

  @Test(expected = MojoExecutionException.class)
  public void testEmptyList() throws MojoExecutionException, IOException {
    final Path tt = target.resolve(UUID.randomUUID().toString());
    final IBRPackageMojo cc = new IBRPackageMojo();
    cc.setWorkDirectory(tt.toFile());
    cc.setFinalName("X");
    cc.setOutputDirectory(tt.resolve(UUID.randomUUID().toString()).toFile());
    cc.setArchiver(new ZipArchiver());
    final File x = cc.createArchive();
    assertNotNull(x);
  }

  @Test
  public void testExceptionPathsAbstractMojo() throws MojoExecutionException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("Error assembling archive");
    m.setWorkDirectory(emptyWorkDirectory.toFile());
    m.createArchive();
  }

  @Test
  public void testExceptionPathsJarFileBasedir() {
    expected.expect(IllegalArgumentException.class);
    expected.expectMessage("basedir is not allowed to be null");
    m.getJarFile(null, null, "type");
  }

  @Test
  public void testExceptionPathsJarFileFinalName() {
    expected.expect(IllegalArgumentException.class);
    expected.expectMessage("finalName is not allowed to be null");
    m.getJarFile(m.getWorkDirectory().toFile(), null, "type");
  }

  @Test
  public void testExceptionPathsWorkDirectoryIsFile() throws MojoExecutionException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("Error assembling archive");
    m.setExistentWorkDirectory(sneakyFile);
    m.createArchive();
  }

  @Test
  public void testExceptionPathsWorkDirectoryNull() throws MojoExecutionException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("Error assembling archive");
    m.setExistentWorkDirectory(null);
    m.createArchive();
  }

  @Test
  public void testExecuteNoArchive() throws MojoExecutionException, MojoFailureException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("List of files to include was empty!");
    m.setProject(null);
    m.execute();
  }

  @Test
  public void testExecuteNoFiles() throws MojoExecutionException, MojoFailureException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("List of files to include was empty!");
    final Map<String, IBRDataObject<JSONObject>> itemMap = new HashMap<>();
    itemMap.put(FakeDummyIBRType.FAKE_TYPE_FILE,
        new IBRDataObject<>(testFakeNoFileCMType, Paths.get("."), builderEntry));

    mavenMap.put(AbstractIBRMojo.COMPILE_ITEMS, itemMap);
    m.setPluginContext(mavenMap);
    m.execute();
  }

  @Test
  public void testExecuteNoTypes() throws MojoExecutionException, MojoFailureException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("No AutomationTypes set for configuration-management-maven-plugin.");
    m.setMyTypes(Collections.emptyMap());
    m.execute();
  }

  @Test
  public void testExecuteSupplementalArtifactException() throws MojoExecutionException, MojoFailureException {
    expected.expect(MojoExecutionException.class);
    expected.expectMessage("List of files to include was empty!");
    m.setClassifier("myClassifier");
    m.getProject().getArtifact().setFile(sneakyFile);
    m.execute();
  }

  @Test(expected = MojoExecutionException.class)
  public void testExecuteWithFile() throws MojoExecutionException, MojoFailureException {
    m.execute();
  }
}
