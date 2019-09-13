#### Targets

`mvn org.infrastructurebuilder.imaging:packer-maven-plugin:packer` will create the packer ami

`mvn org.infrastructurebuilder.imaging:packer-maven-plugin:cleanami` will help clean unused amis. It will prompt for
the `.packer` file created during the build process (`using packer` command) and use that to clean up
the ami and the snapshot. It will prompt for cleaning the ami once it retrieves the details from AWS.
