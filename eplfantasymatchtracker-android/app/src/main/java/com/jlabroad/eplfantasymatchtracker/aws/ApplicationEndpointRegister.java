package com.jlabroad.eplfantasymatchtracker.aws;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.jlabroad.eplfantasymatchtracker.config.GlobalConfig;

import java.util.Map;

public class ApplicationEndpointRegister {

    String _deviceToken;
    String _deviceId;
    AmazonSNS _sns;
    CognitoCachingCredentialsProvider _provider;

    public ApplicationEndpointRegister(String deviceId, String deviceToken, CognitoCachingCredentialsProvider provider) {
        _provider = provider;
        _sns = new AmazonSNSClient(_provider);
        _deviceToken = deviceToken;
        _deviceId = deviceId;
    }

    public void register() {
        Endpoint endpoint = findEndpoint();
        if (endpoint == null) {
            CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest();
            request.setPlatformApplicationArn(GlobalConfig.PlatformApplicationId);
            request.setToken(_deviceToken);
            request.setCustomUserData(_deviceId);
            _sns.createPlatformEndpoint(request);
        }
    }

    protected Endpoint findEndpoint() {
        String keyToFind = createEndpointKey();
        ListEndpointsByPlatformApplicationRequest request = new ListEndpointsByPlatformApplicationRequest();
        request.setPlatformApplicationArn(GlobalConfig.PlatformApplicationId);
        ListEndpointsByPlatformApplicationResult result = _sns.listEndpointsByPlatformApplication(request);
        for (Endpoint endpoint : result.getEndpoints()) {
            for (Map.Entry<String, String> entry : endpoint.getAttributes().entrySet()) {
                if (entry.getValue().equalsIgnoreCase(keyToFind)) {
                    return endpoint;
                }
            }
        }
        return null;
    }

    protected String createEndpointKey() {
        return String.format("%s", _deviceId);
    }
}
