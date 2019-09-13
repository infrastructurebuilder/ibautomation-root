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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PackerCompressPostProcessorTest extends AbstractPackerTestRoot {

  private PackerCompressPostProcessor p;
  private PackerCompressPostProcessor q;

  @Before
  public void setUp1() throws Exception {
    p = new PackerCompressPostProcessor();
    p.setWorkingRootDirectory(getRoot());
    p.setTargetDirectory(getTargetDir());
    q = new PackerCompressPostProcessor();
    q.setArtifact(new DefaultGAV("org.junit:junit:1.2.3:jar"));
    q.setCompressionLevel(6);
    q.setCompressionType(PackerCompressType.tarball);
    q.setOutputFileName("abc");
    q.setWorkingRootDirectory(getRoot());
    q.setTargetDirectory(getTargetDir());
  }

  @Test
  public void testAsJSON() {
    final JSONObject j = new JSONObject("{\"type\":\"compress\",\"compression_level\":6}");
    final JSONObject k = p.asJSON();
    k.remove(PackerConstantsV1.OUTPUT);
    JSONAssert.assertEquals(j, k, true);
    final List<String> keys = q.asJSON().keySet().stream().sorted().collect(Collectors.toList());
    final List<String> just = Arrays.asList("compression_level", "output", "type");
    assertEquals(just, keys);
  }

  @Test
  public void testSetCompressionLevel() {
    p.setCompressionLevel(5);
    assertEquals(5, p.getCompressionLevel());
  }

  @Test
  public void testSetCompressionType() {
    p.setCompressionType(PackerCompressType.lz4ball);
    assertEquals(PackerCompressType.lz4ball, p.getCompressionType());
  }

  @Test
  public void testSetFileName() {
    q.setOutputFileName("ABCD");
    final String s = q.getOutputPath(Optional.of("z")).get().toString();
    assertTrue(s.endsWith("ABCD.z"));
  }

  @Test
  public void testValidate() {

  }

}
