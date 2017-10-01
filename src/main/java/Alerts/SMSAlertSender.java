package Alerts;

import Config.GlobalConfig;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class SMSAlertSender implements IAlertSender {
    private String _phoneNumber;
    private AmazonSNS _client;

    public SMSAlertSender(int teamId, String phoneNumber) {
        _phoneNumber = phoneNumber;
        _client = AmazonSNSClientBuilder.defaultClient();
        CreateTopic(teamId);
    }

    public void SendAlert(int teamId, String text) {
        System.out.format("Sending alert for team %d\n", teamId);
        _client.publish(GetTopicArn(teamId), text);
    }

    private String GetTopicArn(int teamId) {
        return String.format(GlobalConfig.TopicArnFormat, teamId);
    }

    private void CreateTopic(int teamId) {
        new SNSTopicCreator().CreateTopic(teamId);
    }

    private void SubscribeToTopic(int teamId) {
        // TODO
    }
}
