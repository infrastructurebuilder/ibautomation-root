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
package org.infrastructurebuilder.imaging.aws;

import static org.infrastructurebuilder.imaging.PackerConstantsV1.ARTIFACT_ID;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER_TYPE;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILD_TIME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.NAME;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PACKER_RUN_UUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.configuration.management.IBRConstants;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.PackerSizing;
import org.infrastructurebuilder.imaging.maven.PackerManifestTest;
import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class SpecificPackerBaseAWSBuilderTest extends PackerManifestTest {
  private DefaultIBAuthentication a;

  private SpecificPackerBaseAWSBuilder b;

  @Before
  public void setUp3() throws Exception {
    super.setUp2();
    b = new SpecificPackerBaseAWSBuilder();
    a = new DefaultIBAuthentication();
  }

  @Test
  public void testAddRequiredItemsToFactory() {
    b.addRequiredItemsToFactory(a, factory);
  }

  @Test
  public void testGetLookupHint() {
    assertNotNull(b.getLookupHint().get());
  }

  @Test
  public void testGetSizeToStorageMap() {
    assertNotNull(b.getSizeToStorageMap());
  }

  @Test
  public void testMapBuildResult() {
    final JSONObject l = new JSONObject();
    l.put(BUILDER_TYPE, IBRConstants.AMAZONEBS);
    l.put(BUILD_TIME, 100);
    l.put(NAME, "name");
    l.put(PACKER_RUN_UUID, UUID.randomUUID().toString());
    l.put(ARTIFACT_ID,
        "us-east-1:ami-06de16187f0947297,us-east-2:ami-0ac00a7b18d38196b,us-west-1:ami-0be66f8e1e3235437,us-west-2:ami-0e963b51d0c3a483a");
    final ImageBuildResult c2 = b.mapBuildResult(l);
    assertNotNull(c2);
    assertTrue(c2.getArtifactInfo().isPresent());
  }

  @Test
  public void testUpdateBuilderWithInstanceData() {
    b.updateBuilderWithInstanceData(PackerSizing.gpu, a, Optional.empty(), Collections.emptyList(), Optional.empty());
  }

}
