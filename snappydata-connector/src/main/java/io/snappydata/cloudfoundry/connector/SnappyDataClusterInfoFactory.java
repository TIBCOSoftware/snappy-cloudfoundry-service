package io.snappydata.cloudfoundry.connector;

public class SnappyDataClusterInfoFactory {

    public SnappyDataClusterInfo create(SnappyDataServiceInfo serviceInfo) {
        return new SnappyDataClusterInfo(serviceInfo);
    }

}
