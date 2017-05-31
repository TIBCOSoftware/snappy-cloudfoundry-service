
# SnappyData CloudFoundry Service

## Service Broker
Contains the service broker implementation for SnappyData Service, enabling apps on Cloud Foundry to connect to the service.

For more details see its [README](snappydata-service-broker/README.md).

## Spring Cloud CloudFoundry Connector
Provides APIs for Spring apps running on CloudFoundry to easily connect to a SnappyData Service.

For more details, see its [README](snappydata-connector/README.md).

## BOSH Release
Contains the steps to create BOSH release artifacts for SnappyData cluster.

A sample tile manifest YAML, [tile.yml.managed](snappydata-service-broker/tile.yml.managed), is provided to showcase how to package the BOSH release in a PCF tile along with the service broker. If you plan to use this template, rename it as tile.yml.

For more details, see its [README](snappy-managed-service/README.md).
