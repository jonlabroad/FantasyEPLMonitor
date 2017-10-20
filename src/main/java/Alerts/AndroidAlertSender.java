package Alerts;

import Config.GlobalConfig;
import Data.ScoreNotification;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.amazonaws.services.sns.model.PublishRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AndroidAlertSender implements IAlertSender {
    private AmazonSNS _client;

    public AndroidAlertSender() {
        _client = AmazonSNSClientBuilder.defaultClient();
    }

    public void SendAlert(int teamId, ScoreNotification scoreChange) {
        System.out.format("Sending alert for team %d\n", teamId);
        List<String> endpoints =  findTeamEndpoints(teamId);
        for (String endpointArn : endpoints) {
            PublishRequest request = new PublishRequest();
            request.setTargetArn(endpointArn);
            request.setMessage(createNotification(scoreChange));
            request.setMessageStructure("json");
            _client.publish(request);

            request = new PublishRequest();
            request.setTargetArn(endpointArn);
            request.setMessageStructure("json");
            request.setMessage(createDataMessage(scoreChange));
            //_client.publish(request);
        }
    }

    protected List<String> findTeamEndpoints(int teamId) {
        List<String> endpoints = new ArrayList<>();
        ListEndpointsByPlatformApplicationRequest request = new ListEndpointsByPlatformApplicationRequest();
        request.setPlatformApplicationArn(GlobalConfig.Secrets.platformApplicationArn);
        ListEndpointsByPlatformApplicationResult result = _client.listEndpointsByPlatformApplication(request);
        for (Endpoint endpoint : result.getEndpoints()) {
            for (Map.Entry<String, String> attrEntry : endpoint.getAttributes().entrySet()) {
                System.out.println(attrEntry.getKey() + " = " + attrEntry.getValue());
                if (attrEntry.getValue().contains(Integer.toString(teamId))) {
                    endpoints.add(endpoint.getEndpointArn());
                }
            }
        }
        return endpoints;
    }

    protected String createNotification(ScoreNotification scoreChange) {
        String msg = String.format("{ \"GCM\": \"{\\\"notification\\\": {\\\"title\\\": \\\"%s\\\", \\\"body\\\": \\\"%s\\\", \\\"sound\\\": \\\"default\\\", \\\"tag\\\": \\\"0\\\"}}\"}",
                scoreChange.title,
                scoreChange.shortDescription);
        System.out.println(msg);
        return msg;
    }

    protected String createDataMessage(ScoreNotification scoreChange) {
        String msg = String.format("{ \"GCM\": \"{\\\"data\\\": {\\\"title\\\": \\\"%s\\\"}}\"}",
                scoreChange.title);
        return msg;
    }
}