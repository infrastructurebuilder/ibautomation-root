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
package org.infrastructurebuilder.imaging.maven;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.infrastructurebuilder.imaging.AbstractPackerTestRoot;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.file.PackerFileBuilder;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducerFactory;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.infrastructurebuilder.util.executor.DefaultVersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;
import org.json.JSONObject;
import org.junit.Before;
import org.slf4j.Logger;

abstract public class AbstractPackerFactoryTest extends AbstractPackerTestRoot {

  protected final static TestingPathSupplier wps  = new TestingPathSupplier();
  protected VersionedProcessExecutionFactory vpef = new DefaultVersionedProcessExecutionFactory(wps.get(), empty());

  protected static final String             TESTING = "TESTING";
  private Type                              type;
  protected IBAuthenticationProducerFactory apf;
  protected DefaultPlexusContainer          container;
  protected ContainerConfiguration          dpcreq;
  protected PackerFileBuilder               fb;
  protected JSONObject                      fbj;
  protected DefaultGAV                      gav;
  protected PackerImageBuilder              imageBuilder;
  protected ClassWorld                      kw;
  protected ArrayList<PackerManifest>       l;
  protected PackerFactory                   pf;
  protected Properties                      props;

  public AbstractPackerFactoryTest() {
    super();
  }

  @Before
  public void setUp() throws Exception {

    final String mavenCoreRealmId = TESTING;
    kw = new ClassWorld(mavenCoreRealmId, PackerBeanTest.class.getClassLoader());

    dpcreq = new DefaultContainerConfiguration().setClassWorld(kw).setClassPathScanning(PlexusConstants.SCANNING_INDEX)
        .setName(TESTING);
    imageBuilder = new PackerImageBuilder();
    final List<Type> m = new ArrayList<>();
    type = new Type();
    type.setHint("fake");
    m.add(type);
    imageBuilder.setTypes(m);
    container = new DefaultPlexusContainer(dpcreq,
        new WireModule(new SpaceModule(new URLClassSpace(kw.getClassRealm(TESTING)))));
    apf = container.lookup(IBAuthenticationProducerFactory.class, "default");
    apf.setAuthentications(emptyList());
    l = new ArrayList<>();
    gav = new DefaultGAV("X:Y:10.0.0:jar");
    props = new Properties();

    pf = new DefaultPackerFactory(vpef, container, getLog(), target, getRoot(), l, imageBuilder, apf,
        target.resolve("packer"), props, gav, emptyList(), true);
    fb = new PackerFileBuilder();
    fb.setOutputFileName("ABCFILE");
    fb.setContent("ABC");
    pf.addBuilder(fb);
    fbj = fb.asJSON();

  }

  abstract Logger getLog();

}