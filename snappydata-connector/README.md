
# Spring Cloud CloudFoundry Connector for SnappyData Service

This API is currently not published on maven and is being actively refined.

You can publish it locally and access it in your Spring app on CloudFoundry.

1. Build and publish the artifacts locally.

    ```./gradlew clean assemble```

    ```./gradlew install```
    
2. Your application needs to include it as dependency.

    ```compile "io.snappydata:snappydata-cf-connector:0.1-SNAPSHOT"```

## TODO

Provide a sample spring app demonstrating the use of this connector to connect to SnappyData Service.