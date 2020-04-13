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

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isWritable;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.infrastructurebuilder.automation.PackerException.et;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.BUILDER;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.DEFAULT;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.POST_PROCESSOR;
import static org.infrastructurebuilder.imaging.PackerConstantsV1.PROVISIONER;
import static org.infrastructurebuilder.util.IBUtils.writeString;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.infrastructurebuilder.automation.PackerException;
import org.infrastructurebuilder.imaging.IBRHintMap;
import org.infrastructurebuilder.imaging.IBRInternalDependency;
import org.infrastructurebuilder.imaging.ImageBaseObject;
import org.infrastructurebuilder.imaging.ImageBuildResult;
import org.infrastructurebuilder.imaging.ImageBuilder;
import org.infrastructurebuilder.imaging.ImageData;
import org.infrastructurebuilder.imaging.PackerFactory;
import org.infrastructurebuilder.imaging.PackerManifestPostProcessor;
import org.infrastructurebuilder.imaging.PackerPostProcessor;
import org.infrastructurebuilder.imaging.PackerProvisioner;
import org.infrastructurebuilder.imaging.PackerTypeMapperProcessingSection;
import org.infrastructurebuilder.util.ProcessExecutionFactory;
import org.infrastructurebuilder.util.VersionedProcessExecutionFactory;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

public class DefaultPackerFactory implements PackerFactory {
  public final static String naiveFilter(final String d, final String k, final String val) {
    String b = d;
    b = b.replace("@" + k + "@", val);
    b = b.replace("${" + k + "}", val);
    return b;
  }

  public static boolean unwonkeyEquals(final Optional<GAV> a, final Optional<GAV> b) {
    if (a.isPresent() && b.isPresent()) {
      final GAV a1 = a.get();
      final GAV b1 = b.get();
      return a1.getGroupId().equals(b1.getGroupId()) && a1.getArtifactId().equals(b1.getArtifactId())
          && a1.getClassifier().equals(b1.getClassifier()) && a1.getVersion().equals(b1.getVersion())
          && a1.getExtension().equals(b1.getExtension());
    }
    if (!a.isPresent() && !b.isPresent())
      return true;
    return a.equals(b);
  }

  private final VersionedProcessExecutionFactory vpef;
  private final GAV                              artifact;
  private final IBAuthenticationProducerFactory  authProdFactory;
  private List<ImageData>                        builders       = new LinkedList<>();
  private final PlexusContainer                  container;
  private Map<String, Map<String, IBRHintMap>>   hintMap;
  private final Logger                           log;
  private final PackerManifestPostProcessor      manifestPP;
  private final List<PackerManifest>             manifests;
  private final Path                             metaRoot;
  private final Checksum                         packerChecksum;
  private final Path                             packerExecutable;
  private Path                                   packerFile;
  private List<PackerPostProcessor>              postProcessors = new LinkedList<>();
  private final Properties                       props;
  private List<PackerProvisioner>                provisioners   = new LinkedList<>();
  private final List<IBRInternalDependency>      requirements;
  private final Path                             root;

  private final Path target;

  private Map<String, String> variables = new HashMap<>();

  public DefaultPackerFactory(final VersionedProcessExecutionFactory vpef,

      final PlexusContainer container,

      final Logger log,

      final Path metaRoot,

      final Path root,

      final List<PackerManifest> artifactData,

      final ImageBuilder packerImageBuilder,

      final IBAuthenticationProducerFactory authFactory,

      final Path packerExec,

      final Properties p,

      final GAV coords,

      final List<IBRInternalDependency> requirements,

      final boolean copyToOtherRegions) {
    this.vpef = requireNonNull(vpef);
    this.requirements = requireNonNull(requirements);
    this.container = requireNonNull(container);
    authProdFactory = requireNonNull(authFactory);
    this.log = requireNonNull(log);
    this.metaRoot = requireNonNull(metaRoot);
    packerExecutable = requireNonNull(packerExec).toAbsolutePath();
    packerChecksum = new Checksum(packerExecutable);
    props = requireNonNull(p);
    artifact = requireNonNull(coords);
    this.root = et.withReturningTranslation(() -> requireNonNull(root).toAbsolutePath());
    target = requireNonNull(Paths.get(UUID.randomUUID().toString()));
    if (target.isAbsolute())
      throw new PackerException("Target [" + target.toString() + "] must be relative (will be applied to root ["
          + this.root.toString() + "]");
    if (!isDirectory(this.root) || !isWritable(this.root))
      throw new PackerException(this.root + " (temp directory) is not a writable directory");
    final Path path = this.root.resolve(target);
    if (!exists(path)) {
      et.withTranslation(() -> createDirectories(path));
    }
    manifestPP = new PackerManifestPostProcessor();
    manifests = requireNonNull(artifactData);
    update(requireNonNull(manifestPP));
    et.withTranslation(() -> {
      final Map<ImageData, Type> builders = new HashMap<>();

      for (final String hint : packerImageBuilder.getTypeHints()) {
        log.info("Builder -> " + hint);
        final ImageData c = this.container.lookup(ImageData.class, hint);
//        log.debug("  " + hint + " found");
        c.setLog(this.log);
        c.setCopyToOtherRegions(copyToOtherRegions);
        final Type thisType = packerImageBuilder.getTypeFor(hint).get();
        builders.put(c, thisType);
      }
//      if (builders.size() == 0)
//        log.debug(" -> 0 builders");
      for (final ImageData imageData : builders.keySet()) {
        final Type type = builders.get(imageData);
        final Optional<GAV> parent = Optional.ofNullable(type.getParent().orElse(null));
        final String targetType = imageData.getLookupHint().get();
        log.info("  --> " + targetType + " type " + imageData.getPackerType());

        final Set<IBAuthentication> auth = imageData.getAuthType()
            .map(authType -> authProdFactory.getAuthenticationsForType(authType)).orElse(new HashSet<>());
        for (final IBAuthentication a : auth) {
          final ImageData b1 = this.container.lookup(ImageData.class, targetType);
          b1.setCopyToOtherRegions(copyToOtherRegions);
          b1.setLog(getLog());
          b1.setInstanceAuthentication(a);
          b1.setTags(packerImageBuilder.getTags());
//          b1.setOutputFileName(outputFileName); // TODO Maybe set some other settables on the ImageData here?

          final String aKey = a.getId();

          final Map<ImageBuildResult, PackerManifest> mmap = new HashMap<>(1);
          for (final PackerManifest lm : manifests) {
            final Optional<GAV> manifestCoords = lm.getCoords();
            if (Objects.equals(parent, manifestCoords)) {
              final List<ImageBuildResult> items = lm.getBuildsForType(a).orElse(new ArrayList<>());
              for (final ImageBuildResult item : items)
                if (item.getOriginalAuthId().isPresent()) {
                  if (item.getOriginalAuthId().get().equals(aKey)) {
                    mmap.put(item, lm);
                  } else {
                    log.debug(item.getOriginalAuthId().get() + " != " + aKey);
                  }
                } else {
                  log.debug("Original auth id for " + item + " was not present");
                }
            } else {
              log.debug("Parent " + parent + " != " + manifestCoords);
            }
          }
          Optional<ImageBuildResult> m = Optional.empty();
          if (mmap.size() > 1)
            throw new PackerException("Too much tuna! There are " + mmap.size() + " entries");
          if (mmap.size() == 1) {
            m = Optional.of(mmap.keySet().iterator().next());
          }
          if (!b1.getSizes().contains(packerImageBuilder.getSize()))
            throw new PackerException("Size  " + packerImageBuilder.getSize() + " is not available for " + imageData);

          Optional<Type> updateData = Optional.empty();
          final Optional<String> keyPrime = imageData.getLookupHint();

          if (!keyPrime.isPresent()) {
            log.warn(
                "No local type is present for " + imageData.getId() + " so no update data could possibly be available");
          } else {
            final String keyPrimeHint = keyPrime.get();
            updateData = packerImageBuilder.getTypeFor(keyPrimeHint);
            log.info("Update data set to " + updateData);
          }
          b1.updateBuilderWithInstanceData(packerImageBuilder.getSize(), a, m, packerImageBuilder.getDisks(),
              updateData);
          b1.addRequiredItemsToFactory(a, this);
        }
      }
      for (final String ppHint : packerImageBuilder.getPostProcessingHints()) {
        log.info("Postprocessor -> " + ppHint);
        final PackerPostProcessor p1 = this.container.lookup(PackerPostProcessor.class, ppHint);
        log.debug("  " + ppHint + " found");
        p1.setLog(log);
        p1.setTags(packerImageBuilder.getTags());

        if (p1.isMultiAuthCapable()) {
          for (final IBAuthentication a : authProdFactory
              .getAuthenticationsForType(p1.getLookupHint().orElse(DEFAULT))) {
            final PackerPostProcessor p2 = this.container.lookup(PackerPostProcessor.class, ppHint);
            p2.setLog(log);
            p2.setTags(packerImageBuilder.getTags());
            p2.addRequiredItemsToFactory(Optional.of(a), this);
          }
        } else {
          p1.addRequiredItemsToFactory(Optional.empty(), this);
        }
      }
    });
  }

  @Override
  public DefaultPackerFactory addBuilder(final ImageData b) {
    update(requireNonNull(b));
    builders.add(b);
    return this;
  }

  @Override
  public DefaultPackerFactory addPostProcessor(final PackerPostProcessor p) {
    update(requireNonNull(p));
    if (p instanceof PackerManifestPostProcessor)
      throw new PackerException(
          "Manifest processor is injected by the factory.  Use getManifestPath() to read it's value");
    postProcessors.add(requireNonNull(p));
    return this;
  }

  @Override
  public DefaultPackerFactory addProvisioner(final PackerProvisioner p) {
    update(requireNonNull(p));
    provisioners.add(requireNonNull(p));
    return this;
  }

  @Override
  public PackerFactory addUniqueBuilder(final java.util.Comparator<ImageData> b, final ImageData b1) {
    return !builders.stream().filter(x -> b.compare(x, b1) == 0).findFirst().isPresent() ? addBuilder(b1) : this;
  }

  @Override
  public PackerFactory addUniquePostProcessor(final java.util.Comparator<PackerPostProcessor> b,
      final PackerPostProcessor p) {
    return !postProcessors.stream().filter(x -> b.compare(x, p) == 0).findFirst().isPresent() ? addPostProcessor(p)
        : this;
  }

  @Override
  public PackerFactory addUniqueProvisioner(final java.util.Comparator<PackerProvisioner> b,
      final PackerProvisioner p) {
    return !provisioners.stream().filter(x -> b.compare(x, p) == 0).findFirst().isPresent() ? addProvisioner(p) : this;
  }

  @Override
  public PackerFactory addVariable(final String name, final String value) {
    variables.put(requireNonNull(name), requireNonNull(value));
    return this;
  }

  @Override
  public JSONObject asFilteredJSON(final Properties props) {
    final RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
    interpolator.addValueSource(new PropertiesBasedValueSource(requireNonNull(props)));
    try {
      interpolator.addValueSource(new EnvarBasedValueSource());
    } catch (IOException e) {
      log.warn("Failed to use environment variables for interpolation: " + e.getMessage());
    }
    return new JSONObject(
        et.withReturningTranslation(() -> interpolator.interpolate(getBuilderOutputData().toString())));
  }

  @Override
  public Map<String, Path> collectAllForcedOutput() {
    final Map<String, Path> val = new HashMap<>();
    if (packerFile != null) {
      Arrays.asList(builders, postProcessors, provisioners).stream().forEach(list -> {
        list.forEach(item -> {
          item.getForcedOutput().ifPresent(map -> {
            map.keySet().stream().forEach(item2 -> {
              if (val.containsKey(item2)) {
                log.error("Attempting to overwrite a value in the forced output map. IGNORING VALUE.  Key" + item2
                    + " id " + item.getId() + " localtype " + item.getLookupHint());
              } else {
                val.put(item2, root.resolve(map.get(item2)));
              }
            });
          });
        });
      });
    }
    return val;
  }

  @Override
  public Path get() {
    if (packerFile == null) {

      hintMap = new HashMap<>();
      postProcessors.add(manifestPP);

      postProcessors.forEach(p -> p.setArtifact(artifact));
      postProcessors = unmodifiableList(postProcessors);

      hintMap.put(POST_PROCESSOR,
          postProcessors.stream().map(ImageBaseObject::getHintMapForType).collect(toMap(k -> k.getId(), identity())));

      builders = unmodifiableList(builders);
      builders.forEach(p -> p.setArtifact(artifact));

      hintMap.put(BUILDER,
          builders.stream().map(ImageBaseObject::getHintMapForType).collect(toMap(k -> k.getId(), identity())));
      provisioners = unmodifiableList(provisioners);
      provisioners.forEach(p -> p.setArtifact(artifact));
      provisioners.forEach(p -> p.setBuilders(builders));
      hintMap.put(PROVISIONER,
          provisioners.stream().map(ImageBaseObject::getHintMapForType).collect(toMap(k -> k.getId(), identity())));

      variables = Collections.unmodifiableMap(variables);

      packerFile = et.withReturningTranslation(() -> writeString(root.resolve(UUID.randomUUID().toString() + ".json"),
          asFilteredJSON(requireNonNull(props)).toString(2)));

    }
    return packerFile;
  }

  @Override
  public PlexusContainer getContainer() {
    return container;
  }

  @Override
  public Map<String, Map<String, IBRHintMap>> getHintMap() {
    return Optional.ofNullable((Map<String, Map<String, IBRHintMap>>) hintMap)
        .orElseThrow(() -> new PackerException("No hint map in " + this));
  }

  @Override
  public Logger getLog() {
    return log;
  }

  @Override
  public Path getManifestPath() {
    return manifestPP.getOutputPath().orElseThrow(() -> new PackerException("No manifest path available"));
  }

  @Override
  public Path getManifestTargetPath() {
    final Path manifestTarget = getRoot().resolve(getManifestPath());
    if (!exists(manifestTarget.getParent())) {
      et.withTranslation(() -> createDirectories(manifestTarget.getParent()));
    }
    return manifestTarget;
  }

  @Override
  public Path getMetaRoot() {
    return metaRoot;
  }

  @Override
  public Checksum getPackerChecksum() {
    return packerChecksum;
  }

  @Override
  public Path getPackerExecutable() {
    return packerExecutable;
  }

  @Override
  public JSONObject getBuilderOutputData() {
    final JSONObject packerJSON = new JSONObject();
    for (final PackerTypeMapperProcessingSection section : PackerTypeMapperProcessingSection.values()) {
      switch (section) {
        case VARIABLE:
          packerJSON.put(section.getKeyString(), new JSONObject(variables));
          break;
        case BUILDER:
          builders.stream().forEach(x -> x.setLog(getLog()));
          builders.stream().forEach(ImageData::validate);
          packerJSON.put(section.getKeyString(),
              new JSONArray(builders.stream().map(ImageData::asJSON).collect(toList())));
          break;
        case PROVISIONER:
          provisioners.stream().forEach(x -> x.setLog(getLog()));
          provisioners = provisioners.stream().map(pp -> {
            pp.validate();
            return pp.updateWithOverrides(builders);
          }).collect(Collectors.toList());
          packerJSON.put(section.getKeyString(),
              new JSONArray(provisioners.stream().map(PackerProvisioner::asJSON).collect(toList())));
          break;
        case POST_PROCESSOR:
          postProcessors.stream().forEach(x -> x.setLog(getLog()));
          postProcessors.stream().forEach(PackerPostProcessor::validate);

          packerJSON.put(section.getKeyString(),
              new JSONArray(postProcessors.stream().map(PackerPostProcessor::asJSONArray).collect(toList())));
          break;
      }
    }
    return packerJSON;
  }

  @Override
  public List<IBRInternalDependency> getRequirements() {
    return requirements;
  }

  @Override
  public Path getRoot() {
    return root;
  }

  private void update(final ImageBaseObject p) {
    p.setWorkingRootDirectory(root);
    p.setTargetDirectory(target);
  }

  @Override
  public ProcessExecutionFactory getProcessExecutionFactory(String id) {
    return vpef.getDefaultFactory(getRoot(), id, getPackerExecutable().toString()).withChecksum(getPackerChecksum())
        .withRelativeRoot(getMetaRoot());
  }
}
