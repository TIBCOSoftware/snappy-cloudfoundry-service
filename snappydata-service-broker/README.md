# Service Broker

The current implementation of service broker simply returns JDBC connection url of the SnappyData cluster.

## Using the broker

1. You need to have PCF installed. For dev purpose, see [PCF development environment](http://docs.pivotal.io/tiledev/environments.html#pcfdev) setup.

2. Clone the service broker code from [GitHub](https://github.com/SnappyDataInc/snappy-cloudfoundry-service).

3. Update the <checkout-dir>/src/main/resources/application.yml with the details of your SnappyData cluster like hostname, port.

4. Build the broker code.

    ```./gradlew clean assemble```

5. Push the broker as an app to PCF. It deploys it as an app named snappydata-sb-app. See manifest.yml to change the name.

    ```cf push```

6. Register it as a broker. Use the password generated for you when you deployed the broker above.

    ```cf logs --recent snappydata-sb-app | grep password```

    ```cf create-service-broker <name-of-service-broker> user <password> <app-url>```

7. Make the plans of the service visible. SnappyData is the name of the ServiceDefinition.

    ```cf enable-service-access SnappyData```

8. Create a service instance of this broker with one of the plans. Currently, there is only one plan offered with SnappyData.

    ```cf create-service SnappyData snappyData-default-plan <name-of-service-instance>```

9. Bind this service instance to any app running in the PCF. Below command assumes an app named snappy-app is already running in PCF.

    ```cf bind-service snappy-app snappy-service```

10. Finally restart the app so that the credentials of the service are available to it via environment variable VCAP_SERVICES.

    ```cf restart/restage snappy-app```

11. The access credentials will be available to the app via environment variable VCAP_SERVICES. A sample value of which is given below.

    ```
    "VCAP_SERVICES": {
         "SnappyData": [
          {
           "credentials": {
            "jdbcUrl": "jdbc:snappydata://localhost:1527",
            "jobserverUrl": "localhost:8090",
            "password": "password",
            "user": "pcfsnappyuser383112049"
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

## Roadmap

1. Update the service broker when security is fully supported in SnappyData.

2. Provide broker for SnappyData as a managed service on CloudFoundry.
