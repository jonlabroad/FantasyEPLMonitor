package alerts;

import config.DeviceConfig;
import config.EndpointUserData;
import config.GlobalConfig;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.amazonaws.services.sns.model.PublishRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AndroidAlertSender implements IAlertSender {
    private AmazonSNS _client;

    public AndroidAlertSender() {
        _client = AmazonSNSClientBuilder.defaultClient();
    }

    public void sendAlert(int teamId, String title, String subtitle) {
        System.out.format("Sending alert for team %d\n", teamId);
        List<String> endpoints =  findTeamEndpoints(teamId);
        for (String endpointArn : endpoints) {
            PublishRequest request = new PublishRequest();
            request.setTargetArn(endpointArn);
            request.setMessage(createNotification(title, subtitle));
            request.setMessageStructure("json");
            _client.publish(request);

            request = new PublishRequest();
            request.setTargetArn(endpointArn);
            request.setMessageStructure("json");
            request.setMessage(createDataMessage(title));
            _client.publish(request);
        }
    }

    protected List<String> findTeamEndpoints(int teamId) {
        List<String> endpoints = new ArrayList<>();
        ListEndpointsByPlatformApplicationRequest request = new ListEndpointsByPlatformApplicationRequest();
        request.setPlatformApplicationArn(GlobalConfig.Secrets.platformApplicationArn);
        ListEndpointsByPlatformApplicationResult result = _client.listEndpointsByPlatformApplication(request);
        HashSet<String> devices = findDevicesSubscribed(teamId);

        for (Endpoint endpoint : result.getEndpoints()) {
            EndpointUserData userData = readUserData(endpoint.getAttributes().get("CustomUserData"));

            if (devices.contains(userData.uniqueUserId)) {
                endpoints.add(endpoint.getEndpointArn());
            }
        }
        return endpoints;
    }

    protected HashSet<String> findDevicesSubscribed(int teamId) {
        HashSet<String> devices = new HashSet<>();
        for (DeviceConfig config : GlobalConfig.DeviceConfig.values()) {
            devices.addAll(config.getSubscribers(teamId));
        }
        return devices;
    }

    protected String createNotification(String title, String subtitle) {
        String msg = String.format("{ \"GCM\": \"{\\\"notification\\\": {\\\"title\\\": \\\"%s\\\", \\\"body\\\": \\\"%s\\\", \\\"sound\\\": \\\"default\\\", \\\"tag\\\": \\\"0\\\"}}\"}",
                title,
                subtitle);
        System.out.println(msg);
        return msg;
    }

    protected String createDataMessage(String title) {
        String msg = String.format("{ \"GCM\": \"{\\\"data\\\": {\\\"title\\\": \\\"%s\\\"}}\"}",
                title);
        System.out.println(msg);
        return msg;
    }

    protected EndpointUserData readUserData(String userDataRaw) {
        return new EndpointUserData(userDataRaw);
    }
}