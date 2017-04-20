
package io.snappydata.cloudfoundry.connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cloud.service.ServiceInfo;

@Data
@AllArgsConstructor
public class SnappyDataServiceInfo implements ServiceInfo {
    static final String URI_SCHEME = "snappydata";

    private String id;
    private String jdbcUrl;
    private String jobserverUrl;
    private String user;
    private String pass;
}