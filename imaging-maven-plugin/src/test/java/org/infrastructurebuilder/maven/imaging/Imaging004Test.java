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

import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.imaging.maven.MockedPackerBean;
import org.joor.Reflect;
import org.junit.Test;

public class Imaging004Test extends AbstractBuildComponentSetup {

@Test(expected = PackerException.class)
  public void testNulExec() {
    m._validatePackerExecutable();
  }

}
