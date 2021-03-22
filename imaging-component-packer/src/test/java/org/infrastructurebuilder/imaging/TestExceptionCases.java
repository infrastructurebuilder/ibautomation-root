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

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.imaging.file.PackerFileBuilder;
import org.infrastructurebuilder.imaging.maven.DefaultPackerFactory;
import org.infrastructurebuilder.imaging.maven.PackerImageBuilder;
import org.infrastructurebuilder.imaging.maven.PackerManifest;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExceptionCases extends PackerTestingSetup {
  public static final String     BUILD                                 = "build";
  public static final String     COULD_NOT_LOCATE_SUBTYPE_FROM_UNKNOWN = "Could not locate subtype from unknown";
  public static final String     COULD_NOT_LOCATE_TYPE_FROM_UNKNOWN    = "Could not locate type from unknown";
  public static final String     DOES_NOT_EXIST                        = "doesNotExist";
  public static final String     ERROR_MESSAGE                         = "ErrorMessage";
  public final static Logger     log                                   = LoggerFactory
      .getLogger(TestExceptionCases.class);
  public static final String     UNKNOWN                               = "unknown";
  private DefaultPlexusContainer c;
  private PackerFileBuilder      fb;
  private List<PackerManifest>   l;
  private PackerFactory          pf;

  @Before
  public void setupLocally() throws PlexusContainerException, IOException {
    c = new DefaultPlexusContainer();
    l = new ArrayList<>();
    pf = new DefaultPackerFactory(vpef, c, log, targetPath, TEMP, l, new PackerImageBuilder(),
        new DummyNOPAuthenticationProducerFactory(
            () -> IBException.cet.withReturningTranslation(() -> Files.createTempDirectory("A"))),
        targetPath.resolve("packer"), new Properties(), new DefaultGAV("X:Y:1.0.0"), Collections.emptyList(), true);
    fb = new PackerFileBuilder();
    fb.setContent("ABC");
    pf.addBuilder(fb);

  }

}
