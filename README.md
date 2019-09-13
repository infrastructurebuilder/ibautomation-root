# packer-maven-plugin-support
Maven plugin to deal with building images with Hashicorp's Packer

## The Old Way

We were processing `packer.json` files and inferring what _else_ we should do to the elements of it by looking at what was present.  We then parsed all of the output and generated a bespoke JSON manifest.  Authentication was supplied as a "change set" to any number of existing elements of the packer file, and that quickly became unpleasant to work on.

## The Newer, Better, Stronger, Faster Way

1. We are now generating (in their entirety) the `packer.json` files we are executing.
2. We have a very opinionated way of generating these files
3. This opinionated choice allows us to generate many different builders and types, which still allowing execution of custom provisioning and post-processing.
4. The opinionated way does restrict the diversity of the systems that packer can generate, but the reliability of the execution (as it is tied to specific versions of the builder/provisioner/post-processor systems) means that we get more deterministic outcomes.
5. We still use the authentications provided to map the target systems, but since it's all bespoke the resultant packer file is much more parallelizable.
6. Finally, we're discarding the previously generated manifest in favor of a force-injected `manifest` post-processor, which removes quite a lot of code from the processing.

## How to Configure Yourself To Work with the `packer-maven-plugin`

The `packer-maven-plugin` uses the `packer-component` to execute packer and capture results on generated packer files.  In order to make authentication in multiple providers and accounts work properly, it is necessary to supply some credentials to the plugin.  This is accomplished by specifying `<id/>` from `<server/>` entry in `settings.xml`.

Here is an example of configuring the plugin:

```
  <pluginMangement
      <plugins>
        <plugin>
          <groupId>com.deliver8rpacker</groupId>
          <artifactId>packer-maven-plugin</artifactId>
          <version>0.11.0-SNAPSHOT</version>
          <extensions>true</extensions>
          <dependencies>
            <dependency>
              <groupId>com.deliver8r.util.auth</groupId>
              <artifactId>authentication-mapping-component-aws</artifactId>
              <version>0.0.5-SNAPSHOT</version>
            </dependency>
          </dependencies>
          <configuration>
            <packer>${project.build.directory}/packer</packer>
            <embedPackerfile>true</embedPackerfile>
            <timeout>PT40M</timeout>
            <overwrite>true</overwrite>
            <extractDir>${project.build.directory}/dependency</extractDir>
            <authConfig>
              <credentialsTempDirectory>/tmp</credentialsTempDirectory>
              <auths>
                <auth>
                  <id>dev</id>
                  <type>amazon-ebs</type>
                  <target>us-west-2</target>
                  <serverId>dev-uswest-2</serverId>
                </auth>
                <auth>
                  <id>prod</id>
                  <type>amazon-ebs</type>
                  <target>us-west-2</target>
                  <serverId>prod-west2</serverId>
                </auth>
              </auths>
            </authConfig>
            <image>
              <tags>
                <CostCenter>LABS</CostCenter>
                <Platform>Linux</Platform>
              </tags>
              <postProcessors>
                <postProcessor>checksum</postProcessor>
              </postProcessors>
            </image>
          </configuration>
        </plugin>
```

There's a lot to process here, so take it slowly.

### Initial Confis

```
            <packer>${project.build.directory}/packer</packer>
            <embedPackerfile>true</embedPackerfile>
            <timeout>PT40M</timeout>
            <overwrite>true</overwrite>
```

The `packer` executable needs to be a fully qualified path.  This is a requirement for determining version and checksum.

Embed the config file in the output

Set the timeout to 40 minutes.  This is a `Duration.parse(` value.

### Auth

This can be the tricky part

```
            <authConfig>
              <credentialsTempDirectory>/tmp</credentialsTempDirectory>
              <auths>
                <auth>
                  <id>dev</id>
                  <type>amazon-ebs</type>
                  <target>us-west-2</target>
                  <serverId>dev-uswest-2</serverId>
                </auth>
                <auth>
                  <id>prod</id>
                  <type>amazon-ebs</type>
                  <target>us-west-2</target>
                  <serverId>prod-west2</serverId>
                </auth>
              </auths>
            </authConfig>
```

The credentials files for a given auth are written to disk, so it's best to point `<credentialsTempDirectory>` to a RAM disk if possible.  The file is removed after processing, but for a bit the credentials are on disk in plain text.

A number of `<auths>` can be added. Each `<auth>` is a configuration for a target system.  In this case, we have `dev` and `prod`, as indicated by `<id>`.
The `<type>` indicate the `"localType"` of a Packer element that needs credentials.  Currently, `amazon-ebs` works and `docker` is in the works.
The `<target>` for a given auth is specific to the type of `<auth>`. For `amazon-ebs`, the `<target>` is region.  For docker, it is the deployment URL of the target docker registery.
The `<serverId>` indicates a `<server>` in `settings.xml`.  It is required, and will be used to produce username and password for authentication.

### The Image

The Image is built using `<image>`.  Since this example is just something from `<pluginManagement>`, it has no builders specified.

```
            <image>
              <tags>
                <CostCenter>LABS</CostCenter>
                <Platform>Linux</Platform>
              </tags>
              <postProcessors>
                <postProcessor>checksum</postProcessor>
              </postProcessors>
            </image>
```

By convention, `<tags>` are applied to every aspect of the build possible.

`<postProcessor>` entries are the `hint` of a Plexus component for performing some form of post-processing.  The component must be on the classpath at execution time.

`<postProcessor>manifest</postProcessor>` is not a valid post processor, since it is automatically injected at the end of the generated post-processors.

### Real Example

If you have the above `<pluginManagement>` in a parent pom the following works as a module build:

```
      <plugin>
        <groupId>com.deliver8rpacker</groupId>
        <artifactId>packer-maven-plugin</artifactId>
        <configuration>
          <requirements>
          </requirements>
          <image>
            <types>
              <type>deliver8r-amazonebs</type>
            </types>
          </image>
        </configuration>
      </plugin>
```

This will build instances of type `deliver8r-amazonebs` (a specific hint from a configured Plexus component) that produces systems that build the latest updated Centos 6.5 instance that is Yum updatd.  It gets yum updated because `deliver8r-amazonebs` adds a provisioner of type `yum-update-provisioner` as part of it's additions.


### Making new ones

New hints are easy to produce.  You build a project that has `packer-component` as a dependency and then extend the appropriate type, creating a Plexus component from that.


