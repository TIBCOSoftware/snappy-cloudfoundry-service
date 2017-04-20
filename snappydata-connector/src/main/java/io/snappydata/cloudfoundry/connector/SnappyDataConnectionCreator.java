/**
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.snappydata.cloudfoundry.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

public class SnappyDataConnectionCreator extends AbstractServiceConnectorCreator<SnappyDataClusterInfo, SnappyDataServiceInfo> {

    private Logger logger = LoggerFactory.getLogger(SnappyDataConnectionCreator.class);

    @Override
    public SnappyDataClusterInfo create(SnappyDataServiceInfo serviceInfo, ServiceConnectorConfig serviceConnectorConfig) {
        logger.info("Creating snappydata cluster info wth service info: " + serviceInfo);
        try {
            return new SnappyDataClusterInfoFactory().create(serviceInfo);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
