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


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

public class DumpFileToStdOut {
  public static final String SPECIFIC_FIXTURES_FAKE_OUTPUT = "/specific-fixtures/fake_output.txt";

  public static void dump(String[] args) {
    String resource = SPECIFIC_FIXTURES_FAKE_OUTPUT;
    OutputStream os = null;
    PrintStream out = System.out;
    if (args.length > 1) {
      resource = args[1];
    }
    try (

        InputStream ins = DumpFileToStdOut.class.getResourceAsStream(resource);

        Reader r = new InputStreamReader(ins);

        BufferedReader bufferedReader = new BufferedReader(r)) {
      if (args.length > 0) {
        os = new FileOutputStream(args[0], true);
        out = new PrintStream(os);
      }
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        out.println(line);
      }
    } catch (IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    } finally {
      if (out != System.out)
        out.close();
    }
  }

  public static void main(String[] args) {
    dump(args);
  }
}
