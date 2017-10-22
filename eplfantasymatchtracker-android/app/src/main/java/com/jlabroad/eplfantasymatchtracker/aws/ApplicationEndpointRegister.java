package com.jlabroad.eplfantasymatchtracker.aws;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;

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

    public void register(int teamId) {
        Endpoint endpoint = findEndpoint(teamId);
        if (endpoint == null) {
            CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest();
            request.setPlatformApplicationArn("arn:aws:sns:us-east-1:796987500533:app/GCM/EPL_Fantasy_MatchTracker");
            request.setCustomUserData(String.format("%d_%s", teamId, _deviceId));
            request.setToken(_deviceToken);
            _sns.createPlatformEndpoint(request);
        }
    }

    protected Endpoint findEndpoint(int teamId) {
        String keyToFind = createEndpointKey(teamId, _deviceId);
        ListEndpointsByPlatformApplicationRequest request = new ListEndpointsByPlatformApplicationRequest();
        request.setPlatformApplicationArn("arn:aws:sns:us-east-1:796987500533:app/GCM/EPL_Fantasy_MatchTracker");
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

    protected String createEndpointKey(int teamId, String deviceId) {
        return String.format("%d_%s", teamId, deviceId);
    }
}
