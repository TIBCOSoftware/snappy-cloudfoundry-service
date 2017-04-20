

package io.snappydata.cloudfoundry.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

public class SnappyDataServiceInfoCreator extends CloudFoundryServiceInfoCreator<SnappyDataServiceInfo> {

    private Logger logger = LoggerFactory.getLogger(SnappyDataServiceInfoCreator.class);

    public SnappyDataServiceInfoCreator() {
        super(new Tags(SnappyDataServiceInfo.URI_SCHEME), SnappyDataServiceInfo.URI_SCHEME);
    }

    @Override
    public SnappyDataServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        logger.info("Returning snappydata service info: " + serviceData.toString());

        Map<String, Object> credentials = getCredentials(serviceData);
        String id = getId(serviceData);
        String jdbcUrl = credentials.get("jdbcUrl").toString();
        String jobserverUrl = credentials.get("jobserverUrl").toString();
        String user = credentials.get("user").toString();
        String pass = credentials.get("pass").toString();

        return new SnappyDataServiceInfo(id, jdbcUrl, jobserverUrl, user, pass);
    }
}
