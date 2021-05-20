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
package org.infrastructurebuilder.maven.ibr;

import static org.apache.maven.plugins.annotations.InstantiationStrategy.PER_LOOKUP;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;
import static org.apache.maven.plugins.annotations.ResolutionScope.RUNTIME;
import static org.infrastructurebuilder.configuration.management.IBArchiveException.et;
import static org.infrastructurebuilder.ibr.IBRConstants.IBR_METADATA_FILENAME;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.infrastructurebuilder.configuration.management.IBArchive;
import org.infrastructurebuilder.configuration.management.IBArchiveException;
import org.infrastructurebuilder.configuration.management.IBRDataObject;
import org.infrastructurebuilder.configuration.management.IBRValidationOutput;
import org.infrastructurebuilder.util.core.IBUtils;
import org.json.JSONObject;

@Mojo(name = "package", requiresProject = true, threadSafe = true, instantiationStrategy = PER_LOOKUP, defaultPhase = PACKAGE, requiresDependencyResolution = RUNTIME)
public final class IBRPackageMojo extends AbstractIBRMojo {

  private static final String NO_TYPES_HAVE_BEEN_SET = "No IBR Types have been set.";

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (getMyTypes().isEmpty()) {
      getLog().error(NO_TYPES_HAVE_BEEN_SET);
      throw new MojoExecutionException(NO_TYPES_HAVE_BEEN_SET);
    }

    getLog().info("Getting compile order for package run");
    final List<String> order = getCompileOrder()
        .orElseThrow(() -> new MojoExecutionException("No compilation data order available"));
    getLog().info("Getting compile items for package run");
    final Map<String, IBRDataObject<JSONObject>> map = getCompileItems()
        .orElseThrow(() -> new MojoExecutionException("No compilation map data available"));

    getLog().info("Executing...");

    getLog().info("Working in " + getWorkDirectory());

    int total = 0;
    getLog().info("Creating instance of IBArchive");
    final IBArchive ibrArchive = new IBArchive(getOutputDirectory());
    for (final String ord : order) {
      getLog().info("Working on type " + ord);
      final IBRDataObject<JSONObject> v = map.get(ord);
      for (final IBRValidationOutput validation : v.getOutput()) {
        final Path entryRelativized = getWorkDirectory().relativize(validation.getPath());
        final String entryType = v.getBuilder().getType();
        getLog().info(String.format("Adding metadata to IBArchive: %s -> %s", entryType, entryRelativized.toString()));
        ibrArchive.addMetadata(entryType, entryRelativized);
      }

      total += v.getCount();
    }
    getLog().info("Checking for resources to package ");

    getLog().info(String.format("Total files collected to archive: %d", total));
    if (total == 0)
      throw new MojoExecutionException("List of files to include was empty!");

    final Path mdfile = getWorkDirectory().resolve(IBR_METADATA_FILENAME);
    getLog().info("Writing metadata to " + mdfile);
    et.withTranslation(() -> IBUtils.writeString(mdfile, ibrArchive.asJSON().toString(2)));
    try {
      final File a = createArchive();
      getLog().info(String.format("Created archive: %s", a.toString()));
      et.withTranslation(() -> {
        if (getClassifier() != null) {
          getLog().info(String.format("Classifier set: %s", getClassifier()));
          getMavenProjectHelper().attachArtifact(getProject(), "ibr", getClassifier(), a);
        } else {
          if (getProject().getArtifact().getFile() != null && getProject().getArtifact().getFile().isFile())
            throw new IBArchiveException("Classifier requiredto attach supplemental artifacts.");
          getProject().getArtifact().setFile(a);
        }
      });
    } catch (final Exception e) {
      getLog().error("Failed to create archive", e);
      throw new MojoExecutionException("Failed to create archive!");
    }
  }

}
