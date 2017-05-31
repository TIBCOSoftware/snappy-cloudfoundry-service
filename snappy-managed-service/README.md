
# SnappyData BOSH Release

### Deploy locally using BOSH lite

You need to have a BOSH environment to deploy SnappyData as a BOSH release on your local machine. BOSH lite is one option. 

Refer to this page to install [BOSH lite](https://github.com/cloudfoundry/bosh-lite#install-bosh-lite) locally. Once you have BOSH setup on your local machine, refer to below steps create and deploy a SnappyData release. 

1. Download the latest SnappyData release from [GitHub releases](https://github.com/snappydatainc/snappydata/releases) and place it under [blobs/snappydata](blobs/snappydata) directory.

2. Edit the name of snappydata tar file in spec and packaging files under [packages](packages/snappydata) to match the downloaded artifact name.

    Do the same with the monit and templates files of each of the [jobs](jobs).

4. Download [JRE 1.8](http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jre-8u131-linux-i586.tar.gz) and place it under [blobs/jre](blobs/jre). Ensure the name of the tar file matches that in files under packages and jobs directory. 

5. Add the UUID in manifest.yml

    `bosh status --uuid`

6. Set the deployment YAML file. You can edit the manifest file to configure the deployment.

    `bosh deployment manifest.yml`

7. Create the release and upload it to the BOSH Director.

    `bosh create release --force`

    `bosh upload release`

8. Deploy the release.

    `bosh deploy`

9. You can view the deployments as well as VMs created. If any failures, you can ssh to the VMs and check logs.

    The snappydata logs should be available under /var/vcap/data/snappydata-*-bin/work/ in the VMs.  

    `bosh deployments`

    `bosh vms`
    
    `bosh ssh <vm-name>`

At this point, you have successfully deployed your SnappyData cluster as a BOSH release.


### Deploy on PCF via tile

If you have a full PCF installation with Ops Manager, you can deploy the release in that environment via a tile, by wrapping this release into a tile and uploading it to the PCF Ops Manager.

First export the release that you have deployed above using below command, so that you can package it in a tile.

Use the appropriate release and version name and also the stemcell details.
 
    `bosh export release <release-name>/<version> <stemcell-name>/<version>`

For example:

    `bosh export release snappy-managed-service/0+dev.16 ubuntu-trusty/3363.20`

This will create a .tgz file containing the release artifacts.

Include this .tgz file as a path of your `bosh-release` package in your tile.yml. A sample [tile.yml](../snappydata-service-broker/tile.yml.managed) is provided.

