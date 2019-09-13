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

import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.infrastructurebuilder.imaging.PackerException;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducer;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.infrastructurebuilder.util.auth.IBAuthConfigBean;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducerFactory;

public class MockedPackerBean extends PackerBean {
  private final String fail;
  private final Path packerPath;

  public MockedPackerBean(final String type, final Path packerpath) {
    fail = type;
    packerPath = packerpath;
  }

  @Override
  public Optional<Map<String, Path>> executePacker(final String executionId) throws Exception {
    switch (fail) {
    case "hard":
      throw new PackerException("Hard fail");
    case "fail":
      return Optional.empty();
    case "fake":
      final Map<String, Path> fake = new HashMap<>();
      fake.put(PACKER, packerPath);
      return Optional.of(fake);
    default:
      return Optional.of(Collections.emptyMap());
    }
  }

  @Override
  IBAuthenticationProducerFactory setupAuthFactory(IBAuthConfigBean iBAuthConfigBean,
      List<IBAuthentication> list) throws ComponentLookupException {
    DummyNOPAuthenticationProducer writer = new DummyNOPAuthenticationProducer();
    DummyNOPAuthenticationProducerFactory factory = new DummyNOPAuthenticationProducerFactory(
        () -> iBAuthConfigBean.getTempDirectory().resolve(UUID.randomUUID().toString()),
        Arrays.asList(writer));
    factory.setAuthentications(list);
    factory.setTemp(iBAuthConfigBean.getTempDirectory());
    return factory;
  }
}
