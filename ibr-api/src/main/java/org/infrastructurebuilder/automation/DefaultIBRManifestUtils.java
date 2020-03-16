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
package org.infrastructurebuilder.automation;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.infrastructurebuilder.automation.model.v1_0_0.Manifest;
import org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.IBRManifestXpp3Reader;
import org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.IBRManifestXpp3Writer;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;

@Named
@Singleton
public class DefaultIBRManifestUtils {

  private final IBRManifestXpp3Reader reader = new IBRManifestXpp3Reader();
  private final IBRManifestXpp3Writer writer = new IBRManifestXpp3Writer();

  private final AutomationUtils              ibr;
  private final IBRExecutionDataReaderMapper drm;

  @Inject
  public DefaultIBRManifestUtils(AutomationUtils ibr, IBRExecutionDataReaderMapper drm) {
    this.ibr = requireNonNull(ibr);
    this.drm = requireNonNull(drm);
  }

  public AutomationUtils getAutomationUtils() {
    return ibr;
  }

  public Optional<IBRManifest> readManifestFrom(Reader ins) {
    Manifest m;
    synchronized (reader) {
      try {
        m = reader.read(ins).setMapper(drm).clone();
      } catch (IOException | XmlPullParserException e) {
        getAutomationUtils().getLog().error("Failed to read manifest from reader", e);
        m = null;
      }
    }

    return ofNullable(m);

  }

  public Optional<Manifest> recast(IBRManifest m) {
    Manifest m2;
    try {
      m2 = ((Manifest) m).setMapper(drm);
    } catch (Throwable t) {
      getAutomationUtils().getLog().warn("Failed to cast " + m + " to Manifest");
      m2 = null;
    }
    return ofNullable(m2);
  }

  public Optional<String> writeManifest(IBRManifest m) {
    return ofNullable(recast(m).map(manifest -> {
      String mStr;
      synchronized (writer) {
        try (Writer w = new StringWriter()) {
          writer.write(w, manifest.clone());
          w.close();
          mStr = w.toString();
        } catch (IOException e) {
          getAutomationUtils().getLog().error("Error writing manifest " + m , e);
          mStr = null;
        }
      }
      return mStr;
    }).orElse(null));
  }
}
