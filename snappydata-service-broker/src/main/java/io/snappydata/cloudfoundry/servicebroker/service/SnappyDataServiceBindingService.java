package io.snappydata.cloudfoundry.servicebroker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SnappyDataServiceBindingService implements ServiceInstanceBindingService {

    private Logger logger = LoggerFactory.getLogger(SnappyDataServiceBindingService.class);

    private SnappyDataAdminService adminService;

    @Autowired
    public SnappyDataServiceBindingService(SnappyDataAdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        String bindingId = request.getBindingId();
        if (this.adminService.containsBinding(bindingId)) {
            throw new ServiceInstanceBindingExistsException(request.getServiceInstanceId(), bindingId);
        }

        String[] userPass = this.adminService.createUser(bindingId);
        this.logger.info("Created service instance binding for " + request.getServiceInstanceId() + ", " + bindingId);

        Map<String, Object> cred = new HashMap<String, Object>();
        cred.put("jdbcUrl", this.adminService.getConnectionString());
        cred.put("jobserverUrl", this.adminService.getFirstLead());
        cred.put("locatorList", this.adminService.getLocators());
        cred.put("leadList", this.adminService.getLeads());
        cred.put("user", userPass[0]);
        cred.put("password", userPass[1]);
        cred.put("properties", this.adminService.getProperties());
        return new CreateServiceInstanceAppBindingResponse().withCredentials(cred);
    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
        String bindingId = request.getBindingId();
        if (!this.adminService.containsBinding(bindingId)) {
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }
        this.adminService.deleteUser(bindingId);
        logger.info("Deleted service instance binding for " + request.getServiceInstanceId() + ", " + bindingId);
    }
}
