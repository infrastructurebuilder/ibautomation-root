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

import static org.infrastructurebuilder.imaging.container.DockerV1Constants.CONTAINER;
import static org.infrastructurebuilder.imaging.container.DockerV1Constants.DOCKER_TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Optional;

import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerPostProcessor;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerDockerTagPostProcessorTest {

  private DefaultGAV artifact;
  private JSONObject empty;
  private JSONObject full;
  private PackerDockerTagPostProcessor p;

  @Before
  public void setUp() throws Exception {
    p = new PackerDockerTagPostProcessor();
    artifact = new DefaultGAV("X:Y:1.0.0:jar");
    empty = new JSONObject("{\"type\": \"docker-tag\"}");
    full = new JSONObject(
        "{\n" + "  \"type\": \"docker-tag\",\n" + "  \"repository\": \"X/Y\",\n" + "  \"tag\": \"1.0.0\"\n" + "}");
  }

  @Ignore
  @Test
  public void testAddRequiredItemsToFactory() {
    final PackerFactory f = null;
    p.addRequiredItemsToFactory(Optional.empty(), f);
  }

  @Test
  public void testAsJSON() {
    JSONObject k = p.asJSON();
    JSONAssert.assertEquals(empty, k, true);
    p.setArtifact(artifact);
    k = p.asJSON();
    JSONAssert.assertEquals(full, k, true);

  }

  @Test
  public void testAsJSONArray() {
    final JSONArray j1 = new JSONArray(Arrays.asList(empty));
    final JSONArray ja = p.asJSONArray();
    JSONAssert.assertEquals(j1, ja, true);
  }

  @Test
  public void testGetLookupClass() {
    assertEquals(PackerPostProcessor.class, p.getLookupClass());
  }

  @Test
  public void testGetLookupHint() {
    assertEquals(DOCKER_TAG, p.getLookupHint().get());
  }

  @Test
  public void testGetNamedTypes() {
    assertEquals(1, p.getNamedTypes().size());
    assertEquals(CONTAINER, p.getNamedTypes().iterator().next());
  }

  @Test
  public void testGetPackerType() {
    assertEquals(DOCKER_TAG, p.getPackerType());
  }

  @Test
  public void testGetRepository() {
    assertFalse(p.getRepository().isPresent());
    p.setArtifact(artifact);
    assertEquals("X/Y", p.getRepository().get());
  }

  @Test
  public void testGetSaveTarget() {
    assertNotNull(p.getSaveTarget());

  }

  @Test
  public void testGetTag() {
    assertNotNull(p.getTag());
  }

  @Test
  public void testValidate() {
    try {
      p.validate();
    } catch (final PackerException e1) {
      p.setArtifact(artifact);
      p.validate();
    }
  }

}
