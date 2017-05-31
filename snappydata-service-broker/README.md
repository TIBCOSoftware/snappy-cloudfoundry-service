# Service Broker

The current implementation of service broker simply returns JDBC connection url and list of locators and leads of the SnappyData cluster.

## Brokered service

In this case, the SnappyData cluster is running outside the Cloud Foundry and your apps within Cloud Foundry can connect
 to it using the service broker. You need to provide your cluster details to the broker. No BOSH release is required here.

Also, you need to have Cloud Foundry installed. For dev purpose, see [PCF development environment](http://docs.pivotal.io/tiledev/environments.html#pcfdev) setup.
The instructions below have been tried with PCF Dev setup.

* Clone the service broker code from [GitHub](https://github.com/SnappyDataInc/snappy-cloudfoundry-service).

* Update the <checkout-dir>/src/main/resources/application.yml with the details of your SnappyData cluster like hostname, port.

* Build the broker code.

    ```./gradlew clean assemble```

* Push the broker as an app to PCF. It deploys it as an app named snappydata-sb-app. See manifest.yml to change the name.

    ```cf push```

* Register it as a broker. Use the password generated for you when you deployed the broker above.

    ```cf logs --recent snappydata-sb-app | grep password```

    ```cf create-service-broker <name-of-service-broker> user <password> <app-url>```

* Make the plans of the service visible. SnappyData is the name of the ServiceDefinition.

    ```cf enable-service-access SnappyData```

* Create a service instance of this broker with one of the plans. Currently, there is only one plan 'snappyData-default-plan' offered with SnappyData.

    ```cf create-service SnappyData snappyData-default-plan <name-of-service-instance>```

* Bind this service instance to any app running in the PCF. Below, an existing app named snappy-app is bound to the service instance named snappy-service.

    ```cf bind-service snappy-app snappy-service```

* Finally restart the app so that the credentials of the service are available to it via environment variable VCAP_SERVICES.

    `cf restart snappy-app` OR `cf restage snappy-app`

* The access credentials will be available to the app via environment variable VCAP_SERVICES. A sample value of which is given below.

    ```
    "VCAP_SERVICES": {
         "SnappyData": [
          {
           "credentials": {
            "jdbcUrl": "jdbc:snappydata://192.168.16.106:1527",
            "jobserverUrl": "192.168.16.108:8090",
            "leadList": "192.168.16.108:8090,192.168.16.114:8090",
            "locatorList": "192.168.16.106:10334,192.168.16.101:10334",
            "password": "password",
            "properties": "load-balance=true;log-file=snappydataclient.log",
            "user": "SNAPPYUSER4259"
           },
           "label": "SnappyData",
           "name": "snappy-service",
           "plan": "snappyData-default-plan",
           "provider": null,
           "syslog_drain_url": null,
           "tags": [
            "snappydata",
            "spark-database"
           ],
           "volume_mounts": []
          }
         ]
        }
    ```

#### Deploy broker via tile

Above, we saw how to manually push broker as an app to Cloud Foundry via CLI and register it as broker. If you have PCF setup with Ops Manager, you can also deploy the broker via a tile.

Simply refer the build jar as a `app-broker` package in tile.yml and generate the tile artifact to be uploaded to Ops Manager.
 
* Refer to a sample [tile.yml](tile.yml.brokered) template. Rename it as tile.yml.

* Using tile-generator tool, generate the .pivotal file.

    `tile build`
    
    This will process your tile.yml file and generate .pivotal file under product directory.

* Log into your Ops Manager and import the .pivotal file, add it as a tile in the dashboard, configure and hit 'Apply changes'.

    On successful completion of applying changes, you'll see your broker registered. All you have to do now is create a service instance and bind it to your apps.
    
    `cf create-service SnappyData snappyData-default-plan <name-of-service-instance>`
    
    `cf bs snappy-app snappy-service`
 
## Managed Service

With Brokered Service, we had to run and manage the SnappyData cluster outside the Cloud Foundry.

But you can also deploy SnappyData cluster as a managed service i.e. a service run and managed within the Cloud Foundry and enable apps connect to it via this broker.

While the service broker does not change, the SnappyData cluster, though, needs to be deployed as a BOSH release. 

Refer to this [readme](../snappy-managed-service/README.md) for more details on how to create the BOSH release. Then, add the BOSH release as one of the `packages` in your tile.yml with `type` as `bosh-release`. A sample [tile.yml](tile.yml.managed) is provided.

Once the tile is uploaded and successfully installed, it'll launch the SnappyData cluster as well as register the broker on PCF.

You'll need to create a service instance using CLI and bind it to your app (this will be automated soon).

## Roadmap

* Update the service broker when security is fully supported in SnappyData.
