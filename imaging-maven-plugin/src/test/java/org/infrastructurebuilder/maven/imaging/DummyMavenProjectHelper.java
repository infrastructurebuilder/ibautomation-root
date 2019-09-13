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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

public class DummyMavenProjectHelper implements MavenProjectHelper {

  Map<MavenProject, Map<String, File>> artifactsClassified = new HashMap<>();
  Map<MavenProject, Map<String, File>> artifactsTyped = new HashMap<>();
  Map<MavenProject, List<Tuple3<String, List<String>, List<String>>>> resources = new HashMap<>();
  Map<MavenProject, List<Tuple3<String, List<String>, List<String>>>> testResources = new HashMap<>();

  @Override
  public void addResource(final MavenProject project, final String resourceDirectory, final List<String> includes,
      final List<String> excludes) {
    if (!resources.containsKey(project)) {
      resources.put(project, new ArrayList<>());
    }
    resources.get(project).add(Tuple.tuple(resourceDirectory, includes, excludes));

  }

  @Override
  public void addTestResource(final MavenProject project, final String resourceDirectory, final List<String> includes,
      final List<String> excludes) {
    if (!testResources.containsKey(project)) {
      testResources.put(project, new ArrayList<>());
    }
    testResources.get(project).add(Tuple.tuple(resourceDirectory, includes, excludes));
  }

  @Override
  public void attachArtifact(final MavenProject project, final File artifactFile, final String artifactClassifier) {
    if (!artifactsClassified.containsKey(project)) {
      artifactsClassified.put(project, new HashMap<>());
    }
    artifactsClassified.get(project).put(artifactClassifier, artifactFile);
  }

  @Override
  public void attachArtifact(final MavenProject project, final String artifactType, final File artifactFile) {
    if (!artifactsTyped.containsKey(project)) {
      artifactsTyped.put(project, new HashMap<>());
    }
    artifactsTyped.get(project).put(artifactType, artifactFile);
  }

  @Override
  public void attachArtifact(final MavenProject project, final String artifactType, final String artifactClassifier,
      final File artifactFile) {
    attachArtifact(project, artifactFile, artifactClassifier);
    attachArtifact(project, artifactType, artifactFile);
  }

  public Map<MavenProject, Map<String, File>> getArtifactsClassified() {
    return artifactsClassified;
  }

  public Map<MavenProject, Map<String, File>> getArtifactsTyped() {
    return artifactsTyped;
  }

  public Map<MavenProject, List<Tuple3<String, List<String>, List<String>>>> getResources() {
    return resources;
  }

  public Map<MavenProject, List<Tuple3<String, List<String>, List<String>>>> getTestResources() {
    return testResources;
  }

}
