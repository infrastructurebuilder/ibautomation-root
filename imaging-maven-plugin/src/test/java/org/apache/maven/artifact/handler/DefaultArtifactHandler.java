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
package org.apache.maven.artifact.handler;

import javax.inject.Named;

@Named
public class DefaultArtifactHandler implements ArtifactHandler {
  private boolean addedToClasspath;

  private String classifier;

  private String directory;

  private String extension;

  private boolean includesDependencies;

  private String language;

  private String packaging;

  private String type;

  public DefaultArtifactHandler() {
  }

  public DefaultArtifactHandler(final String type) {
    this.type = type;
  }

  @Override
  public String getClassifier() {
    return classifier;
  }

  @Override
  public String getDirectory() {
    if (directory == null) {
      directory = getPackaging() + "s";
    }
    return directory;
  }

  @Override
  public String getExtension() {
    if (extension == null) {
      extension = type;
    }
    return extension;
  }

  @Override
  public String getLanguage() {
    if (language == null) {
      language = "none";
    }

    return language;
  }

  @Override
  public String getPackaging() {
    if (packaging == null) {
      packaging = type;
    }
    return packaging;
  }

  public String getType() {
    return type;
  }

  @Override
  public boolean isAddedToClasspath() {
    return addedToClasspath;
  }

  @Override
  public boolean isIncludesDependencies() {
    return includesDependencies;
  }

  public void setAddedToClasspath(final boolean addedToClasspath) {
    this.addedToClasspath = addedToClasspath;
  }

  public void setExtension(final String extension) {
    this.extension = extension;
  }

  public void setIncludesDependencies(final boolean includesDependencies) {
    this.includesDependencies = includesDependencies;
  }

  public void setLanguage(final String language) {
    this.language = language;
  }

}
