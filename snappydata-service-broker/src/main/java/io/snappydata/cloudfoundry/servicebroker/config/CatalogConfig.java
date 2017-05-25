package io.snappydata.cloudfoundry.servicebroker.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogConfig {

    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        "snappydata-service-broker",
                        "SnappyData",
                        "SnappyData service broker implementation for Community Edition",
                        true,
                        true,
                        Collections.singletonList(
                                new Plan("snappydata-default",
                                        "snappyData-default-plan",
                                        "This is a default SnappyData plan.",
                                        getPlanMetadata())),
                        Arrays.asList("snappydata", "spark-database"),
                        getServiceDefinitionMetadata(),
                        null,
                        null)));
    }

/* Used by Pivotal CF console */

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap();
        sdMetadata.put("displayName", "SnappyData");
        sdMetadata.put("imageUrl", "http://www.snappydata.io/assets/images/favicon/favicon-120.png");
        sdMetadata.put("longDescription", "SnappyData Service");
        sdMetadata.put("providerDisplayName", "Pivotal");
        sdMetadata.put("documentationUrl", "https://github.com/snappydatainc/snappy-cloudfoundry-service");
        sdMetadata.put("supportUrl", "https://github.com/snappydatainc/snappy-cloudfoundry-service");
        return sdMetadata;
    }

    private Map<String, Object> getPlanMetadata() {
        Map<String, Object> planMetadata = new HashMap();
        planMetadata.put("costs", getCosts());
        planMetadata.put("bullets", getBullets());
        return planMetadata;
    }

    private List<Map<String, Object>> getCosts() {
        Map<String, Object> costsMap = new HashMap();

        Map<String, Object> amount = new HashMap();
        amount.put("usd", 0.0);

        costsMap.put("amount", amount);
        costsMap.put("unit", "MONTHLY");

        List costMapList = Collections.singletonList(costsMap);
        return costMapList;
    }

    private List<String> getBullets() {
        return Arrays.asList("Shared SnappyData cluster");
    }

}