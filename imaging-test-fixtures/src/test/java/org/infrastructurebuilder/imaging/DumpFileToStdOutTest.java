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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.infrastructurebuilder.imaging.DumpFileToStdOut;
import org.infrastructurebuilder.util.IBUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DumpFileToStdOutTest extends PackerTestingSetup {

  private static final String ABC = "ABC";
  private static final String ABC_IN = "/ABC.in";
  private static final String X_OUT = "X.out";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  private Path tgt;

  @Before
  public void setUp() throws Exception {
    tgt = TARGET.resolve(X_OUT);
    try {
      Files.delete(tgt);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testMain() throws IOException {
    assertEquals("Equal", ABC, IBUtils.readToString(DumpFileToStdOut.class.getResourceAsStream(ABC_IN)));
    String[] s = { tgt.toAbsolutePath().toString(), ABC_IN };
    DumpFileToStdOut.main(s);
    List<String> lines = Files.readAllLines(tgt);
    assertEquals("1 line", 1, lines.size());
    assertEquals("In", ABC, lines.get(0));
  }

  @Test(expected=NullPointerException.class)
  public void testMainNoArgs() throws IOException {
    new DumpFileToStdOut();
    DumpFileToStdOut.main(new String[0]);
  }

}
