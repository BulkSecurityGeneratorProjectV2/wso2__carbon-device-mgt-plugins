/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.services.android.services.policymgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.services.policymgt.PolicyMgtService;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class PolicyMgtServiceImpl implements PolicyMgtService {
    private static Log log = LogFactory.getLog(PolicyMgtService.class);

    @GET
    @Path("{deviceId}")
    public Response getEffectivePolicy(@HeaderParam("Accept") String acceptHeader,
                                      @PathParam("deviceId") String id) throws AndroidAgentException {

        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message responseMessage = new Message();
        Policy policy;
        try {
            PolicyManagerService policyManagerService = AndroidAPIUtils.getPolicyManagerService();
            policy = policyManagerService.getEffectivePolicy(deviceIdentifier);
            if (policy == null) {
                responseMessage = Message.responseMessage("No effective policy found").
                        responseCode(Response.Status.NO_CONTENT.toString()).build();
                return Response.status(Response.Status.NO_CONTENT).entity(responseMessage).type(
                        responseMediaType).build();
            } else {
                responseMessage = Message.responseMessage("Effective policy added to operation").
                        responseCode(Response.Status.OK.toString()).build();
                return Response.status(Response.Status.OK).entity(responseMessage).type(
                        responseMediaType).build();
            }
        } catch (PolicyManagementException e) {
            String msg = "Error occurred while getting the policy.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).type(
                    responseMediaType).build();
        }
    }

    @GET
    @Path("/features/{deviceId}")
    public Response getEffectiveFeatures(@HeaderParam("Accept") String acceptHeader,
                                                     @PathParam("deviceId") String id) throws AndroidAgentException {
        List<ProfileFeature> profileFeatures;
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        try {
            PolicyManagerService policyManagerService = AndroidAPIUtils.getPolicyManagerService();
            profileFeatures = policyManagerService.getEffectiveFeatures(deviceIdentifier);
            if (profileFeatures == null) {
                Response.status(Response.Status.NOT_FOUND);
                return Response.status(Response.Status.NOT_FOUND).type(
                        responseMediaType).build();
            }
        } catch (FeatureManagementException e) {
            String msg = "Error occurred while getting the features.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).type(
                    responseMediaType).build();
        }
        return Response.status(Response.Status.OK).entity(profileFeatures).type(
                responseMediaType).build();
    }
}
