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
package org.infrastructurebuilder.configuration.management.ansible;

import static org.infrastructurebuilder.configuration.management.ansible.AnsibleConstants.ANSIBLE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.configuration.management.DefaultIBConfigSupplier;
import org.infrastructurebuilder.configuration.management.IBConfigSupplier;
import org.infrastructurebuilder.configuration.management.IBRConstants;
import org.infrastructurebuilder.ibr.utils.AutomationUtilsTesting;
import org.infrastructurebuilder.imaging.DefaultFakeImageData;
import org.infrastructurebuilder.util.IBUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class AnsibleIBRTypeTest {

  private IBConfigSupplier acs;
  private Map<String, String> params;
  private AutomationUtilsTesting rps;
  private AnsibleIBRType t;

  @Before
  public void setUp() throws Exception {
    rps = new AutomationUtilsTesting();
    acs = new DefaultIBConfigSupplier().setConfig(new HashMap<>());
    t = new AnsibleIBRType(rps, Arrays.asList(new DefaultAnsibleIBRValidator(rps, new DefaultAnsibleValidator())));
    t.setConfigSupplier(acs);
    assertNotNull(t);
  }

  @Test
  public void testGetValidators() {
    assertFalse(t.getValidators().size() == 0);
  }

  @Test
  public void testTransformToProvisionerEntry() throws JSONException, IOException {
    final Path p = Paths.get("abc.xml").toAbsolutePath();
    final JSONObject j = new JSONObject(IBUtils.readToString(getClass().getResourceAsStream("/testCMType.json")));
    final JSONObject g = t.transformToProvisionerEntry(ANSIBLE, null, p, null, Collections.emptyList());
    JSONAssert.assertEquals(j, g, true);
  }

  @Test
  public void testTransformToProvisionerEntryWithOverrides() throws JSONException, IOException {
    final Path p = Paths.get("abc.xml").toAbsolutePath();
    final JSONObject j = new JSONObject(
        "{\"type\":\"ansible\",\"playbook_file\":\"../../abc.xml\",\"override\":{\"test\":{\"user\":\"X\"}}}");
    final JSONObject g = t.transformToProvisionerEntry(ANSIBLE, null, p, null,
        Arrays.asList(new DefaultFakeImageData(IBRConstants.AMAZONEBS, Optional.of("X"))));
    JSONAssert.assertEquals(j, g, true);
  }

}
