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
package org.infrastructurebuilder.maven.imaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.joor.Reflect;
import org.junit.Test;

public class ImagingComp002Test extends AbstractImagingMojoSetup {

  @Test(expected = MojoExecutionException.class)
  public void testExecuteBogusExecutionId() throws MojoExecutionException, MojoFailureException {
    exec = new MojoExecution(null, "executionId");
    Reflect.on(m).set("mojo", exec);
    m.packerExecutable = target.resolve("packer").toAbsolutePath().toFile();
    m.execute();
  }

  @Test
  public void testExecuteSkip() {
    m.packerExecutable = target.resolve("packer").toAbsolutePath().toFile();
    m.skip = true;
    try {
      m.execute();
    } catch (MojoExecutionException | MojoFailureException e) {
      fail("You suck at all games!");
    }
  }

  @Test
  public void testMinorSetters() {
    m.encoding = "UTF_16";
    assertEquals("UTF_16", m.encoding);
    m.timeout = "PT10S";
    assertEquals("PT10S", m.timeout);
    final File f = target.resolve("f").toFile();
    m.varFile = f;
    assertEquals(f, m.varFile);
    final Map<String, String> vars = Collections.emptyMap();
    m.vars = vars;
    assertEquals(vars, m.vars);
    assertFalse(m.parallel);
    assertFalse(m.cleanupOnError);
    assertFalse(m.force);
    assertTrue(m.overwrite);
    assertFalse(m.skipIfEmpty);
  }

}
