package io.snappydata.cloudfoundry.connector;

public class SnappyDataClusterInfo {

    private SnappyDataServiceInfo info;

    public SnappyDataClusterInfo(SnappyDataServiceInfo serviceInfo) {
        this.info = serviceInfo;
    }

    public String getJdbcUrl() {
        return info.getId();
    }

    public String getJobserverUrl() {
        return info.getId();
    }

}
