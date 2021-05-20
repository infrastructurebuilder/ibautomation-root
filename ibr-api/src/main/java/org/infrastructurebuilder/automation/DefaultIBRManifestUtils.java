/*
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
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.codehaus.plexus.util.xml.Xpp3DomBuilder.build;
import static org.infrastructurebuilder.automation.IBRAutomationException.et;
import static org.infrastructurebuilder.util.core.IBUtils.stringFromDocument;
import static org.infrastructurebuilder.util.core.IBUtils.writeString;
import static org.infrastructurebuilder.util.constants.IBConstants.XML;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.System.Logger;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.infrastructurebuilder.automation.model.v1_0_0.Dependency;
import org.infrastructurebuilder.automation.model.v1_0_0.DependentExecution;
import org.infrastructurebuilder.automation.model.v1_0_0.Manifest;
import org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.IBRManifestXpp3Reader;
import org.infrastructurebuilder.automation.model.v1_0_0.io.xpp3.IBRManifestXpp3Writer;
import org.infrastructurebuilder.ibr.utils.AutomationUtils;
import org.w3c.dom.Document;

@Named
@Singleton
public class DefaultIBRManifestUtils implements IBRManifestUtils {

  private final IBRManifestXpp3Reader        reader = new IBRManifestXpp3Reader();
  private final IBRManifestXpp3Writer        writer = new IBRManifestXpp3Writer();

  private final AutomationUtils              ibr;
  private final IBRExecutionDataReaderMapper drm;

  public final static Xpp3Dom xpp3DomFromDocument(Document document) {
    return et.withReturningTranslation(() -> build(new StringReader(stringFromDocument(document))));
  }

  @Inject
  public DefaultIBRManifestUtils(AutomationUtils ibr, IBRExecutionDataReaderMapper drm) {
    this.ibr = requireNonNull(ibr);
    this.drm = requireNonNull(drm);
  }

  @Override
  public AutomationUtils getAutomationUtils() {
    return ibr;
  }

  @Override
  public Optional<IBRManifest> readManifestFrom(Reader ins) {
    Manifest m;
    synchronized (reader) {
      try {
        m = reader.read(ins).setMapper(drm).clone();
      } catch (IOException | XmlPullParserException e) {
        getAutomationUtils().getLog().log(Logger.Level.ERROR, "Failed to read manifest from reader", e);
        m = null;
      }
    }

    return ofNullable(m);

  }

  private Optional<Manifest> recast(IBRManifest m) {
    Manifest m2;
    try {
      m2 = ((Manifest) m).setMapper(drm);
    } catch (Throwable t) {
      getAutomationUtils().getLog().log(Logger.Level.WARNING, "Failed to cast " + m + " to Manifest");
      m2 = null;
    }
    return ofNullable(m2);
  }

  private DependentExecution recast(IBRDependentExecution de, Manifest parent) {
    if (de instanceof DependentExecution) {
      return (DependentExecution) de;
    }
    DependentExecution d = new DependentExecution();
    d.setParent(requireNonNull(parent));
    d.setExecutionData(
        de.getExecutionData().orElseThrow(() -> new IBRAutomationException("No Execution data available for " + de)));
    d.setLogLines(de.getLogLines());
    d.setGav(new Dependency(de.getGav()));
    return d;
  }

  @Override
  public Optional<Path> writeManifest(IBRManifest m, List<? extends IBRDependentExecution> additionalExections) {
    return ofNullable(
        //
        recast(m).map(manifest -> {
          manifest
              .setExec(concat(manifest.getDependentExecutions().parallelStream().map(rr -> this.recast(rr, manifest)),
                  requireNonNull(additionalExections).parallelStream().map(rr -> this.recast(rr, manifest)))
                      .map(DependentExecution::clone).collect(toList()));
          String mStr;
          synchronized (writer) {
            try (Writer w = new StringWriter()) {
              writer.write(w, manifest.clone()); // The clone within the models sets up the Xpp3Dom of the dependent
                                                 // items
              w.close();
              mStr = w.toString();
            } catch (IOException e) {
              getAutomationUtils().getLog().log(Logger.Level.ERROR, "Error writing manifest " + m, e);
              mStr = null;
            }
          }
          return mStr;
        }).orElse(null)).map(string -> et.withReturningTranslation(
            () -> writeString(getAutomationUtils().getWorkingPath().resolve(randomUUID().toString() + XML), string)));
  }
}
