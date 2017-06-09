
# SnappyData BOSH Release

### Deploy locally using BOSH lite

You need to have a BOSH environment to deploy SnappyData as a BOSH release on your local machine. BOSH lite is one option. 

Refer to this page to install [BOSH lite](https://github.com/cloudfoundry/bosh-lite#install-bosh-lite) locally. Once you have BOSH setup on your local machine, refer to below steps to create and deploy a SnappyData release.

* Download the latest SnappyData release from [GitHub releases](https://github.com/snappydatainc/snappydata/releases) and place it under blobs/snappydata directory. Create this directory under [snappy-manged-srevice](.), if not present.

* Edit the name of snappydata tar file in spec and packaging files under [packages](packages/snappydata) to match the downloaded artifact name.

    Do the same with the monit and templates files of each of the [jobs](jobs).

* Download [JRE 1.8](http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jre-8u131-linux-i586.tar.gz) and place it under [blobs/jre](blobs/jre). Ensure the name of the tar file matches that in files under packages and jobs directory. 

* Set the `director_uuid` in manifest.yml. You can get it from bosh status command.

    `bosh status --uuid`

* Set the manifest.yml as deployment YAML file. Before that, you may edit the manifest file to configure the deployment as needed.

    `bosh deployment manifest.yml`

* Create the release and upload it to the BOSH Director.

    `bosh create release --force`

    `bosh upload release`

* Deploy the release.

    `bosh deploy`

* You can view the deployments as well as VMs created. If any failures, you can ssh to the VMs and check logs.

    The snappydata logs should be available under /var/vcap/data/snappydata-*-bin/work/ in the VMs.  

    `bosh deployments`

    `bosh vms`
    
    `bosh ssh <vm-name>`

At this point, you have successfully deployed your SnappyData cluster as a BOSH release.


### Deploy on PCF via tile

If you have a full PCF installation with Ops Manager, you can also deploy the release in that environment via a tile, by wrapping this release into a tile and uploading it to the PCF Ops Manager.

First, create a tarball of the release that you have deployed above using below command, so that you can package it in a tile.

Use the appropriate release and version name and also the stemcell details.
 
    `bosh export release <release-name>/<version> <stemcell-name>/<version>`

For example:

    `bosh export release snappy-managed-service/0+dev.16 ubuntu-trusty/3363.20`

This will create a .tgz file containing the release artifacts.

Include this .tgz file as a path of your `bosh-release` package in your tile.yml. A sample [tile.yml](../snappydata-service-broker/tile.yml.managed) is provided.

