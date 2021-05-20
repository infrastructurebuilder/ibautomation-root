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
package org.infrastructurebuilder.imaging.maven;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.file.PackerFileBuilder;
import org.infrastructurebuilder.imaging.shell.PackerShellLocalProvisioner;
import org.infrastructurebuilder.imaging.shell.PackerShellPostProcessor;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class PackerFactoryTest extends AbstractPackerFactoryTest {
  public final static Logger log = System.getLogger(PackerFactoryTest.class.getName());

  @Test
  public void testAddBuilder() {

    JSONObject j = pf.getBuilderOutputData();
    JSONObject jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [],\n" + "  \"post-processors\": []\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
    final PackerFileBuilder j2 = new PackerFileBuilder();
    j2.setContent("ABC");
    j2.setTargetDirectory(getTargetDir());
    pf.addBuilder(j2);
    final JSONObject fj = j2.asJSON();
    j = pf.getBuilderOutputData();
    jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "," + fj + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [],\n" + "  \"post-processors\": []\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
  }

  @Test
  public void testAddPostProcessor() {
    JSONObject j = pf.getBuilderOutputData();
    JSONObject jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [],\n" + "  \"post-processors\": []\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
    final PackerShellPostProcessor j2 = new PackerShellPostProcessor();
    j2.setInlines(Arrays.asList("#", "#"));
    j2.setTargetDirectory(getTargetDir());
    pf.addPostProcessor(j2);
    final JSONArray fj = j2.asJSONArray();
    j = pf.getBuilderOutputData();
    jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [],\n" + "  \"post-processors\": [" + fj + "]\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
  }

  @Test
  public void testAddProvisioner() {
    JSONObject j = pf.getBuilderOutputData();
    JSONObject jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [],\n" + "  \"post-processors\": []\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
    final PackerShellLocalProvisioner j2 = new PackerShellLocalProvisioner();
    j2.setInlines(Arrays.asList("#", "#", "#"));
    j2.setTargetDirectory(getTargetDir());
    pf.addProvisioner(j2);

    final JSONObject fj = j2.asJSON();
    j = pf.getBuilderOutputData();
    jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [" + fj + "],\n" + "  \"post-processors\": []\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
  }

  @Test
  public void testAddVariable() {
    JSONObject j = pf.getBuilderOutputData();
    JSONObject jj = new JSONObject("{\n" + "  \"builders\": [" + fbj.toString() + "],\n" + "  \"variables\": {},\n"
        + "  \"provisioners\": [],\n" + "  \"post-processors\": []\n" + "}");
    JSONAssert.assertEquals(jj, j, true);
    pf.addVariable("X", "Y");
    j = pf.getBuilderOutputData();
    jj = new JSONObject("{\"builders\":[" + fbj.toString()
        + "],\"variables\":{\"X\":\"Y\"},\"provisioners\":[],\"post-processors\":[]}");
    JSONAssert.assertEquals(jj, j, true);
  }

  @Test
  public void testCollectAllForcedOutput() {
    final Map<String, Path> ppf = pf.collectAllForcedOutput();
    assertNotNull(ppf);
  }

  @Ignore
  @Test(expected = PackerException.class)
  public void testFailAbsolute() {
    new DefaultPackerFactory(vpef, container, log, target, getRoot(), l, imageBuilder, apf, target.resolve("packer"), props,
        gav, emptyList(), true);
  }

  @Test(expected = PackerException.class)
  public void testFailNonDir() {
    new DefaultPackerFactory(vpef, container, log, target, target.resolve("packer"), l, imageBuilder, apf,
        target.resolve("packer"), props, gav, emptyList(), true);
  }

  @Test
  public void testGetPackerFile() throws IOException {
    final PackerFileBuilder fb = new PackerFileBuilder();
    fb.setOutputFileName("filename.txt");
    fb.setContent("ABC");
    pf.addBuilder(fb);

    final Path g = pf.get();
    final JSONObject j = new JSONObject(String.join("\n", Files.readAllLines(g)));
    JSONAssert.assertEquals(pf.getBuilderOutputData(), j, true);
  }

  @Test
  public void testNaiveFilteR() {
    assertEquals("ACC", DefaultPackerFactory.naiveFilter("A${B}@B@", "B", "C"));
  }

  @Test
  public void testUEquals() {
    GAV a = new DefaultGAV("x:y:1:jar:b");
    GAV c = new DefaultGAV("zz:y:1:jar:b");
    assertTrue(DefaultPackerFactory.unwonkeyEquals(Optional.empty(), Optional.empty()));
    assertFalse(DefaultPackerFactory.unwonkeyEquals(Optional.of(a), Optional.of(c)));
    assertFalse(DefaultPackerFactory.unwonkeyEquals(Optional.of(a), Optional.empty()));
  }

  @Override
  Logger getLog() {
    return log;
  }

}
