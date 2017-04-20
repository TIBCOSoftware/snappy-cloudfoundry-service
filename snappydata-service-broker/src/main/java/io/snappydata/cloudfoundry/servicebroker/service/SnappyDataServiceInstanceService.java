package io.snappydata.cloudfoundry.servicebroker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SnappyDataServiceInstanceService implements ServiceInstanceService {

    private SnappyDataAdminService adminService;

    private Logger logger = LoggerFactory.getLogger(SnappyDataServiceInstanceService.class);

    @Autowired
    public SnappyDataServiceInstanceService(SnappyDataAdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        if (this.adminService.containsInstance(instanceId)) {
            throw new ServiceInstanceExistsException(instanceId, request.getServiceDefinitionId());
        }
        this.adminService.addInstance(instanceId);
        this.logger.info("Created service instance " + instanceId);
        return new CreateServiceInstanceResponse();
    }

    @Override
    public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
        return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        if (!this.adminService.containsInstance(instanceId)) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }
        this.adminService.removeInstance(instanceId);
        this.logger.info("Deleted service instance " + instanceId);
        return new DeleteServiceInstanceResponse();
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        if (!this.adminService.containsInstance(instanceId)) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }
        // Nothing to update here.
        this.logger.info("Updated service instance " + instanceId);
        return new UpdateServiceInstanceResponse();
    }
}
