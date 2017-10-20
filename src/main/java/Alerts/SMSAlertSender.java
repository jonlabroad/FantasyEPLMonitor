package Alerts;

import Config.GlobalConfig;
import Data.ScoreNotification;
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

    public void SendAlert(int teamId, ScoreNotification scoreChange) {
        System.out.format("Sending alert for team %d\n", teamId);
        // This shouldn't actually be used, just put it in here to get it to compile
        for (String event : scoreChange.getTickerEvents()) {
            _client.publish(GetTopicArn(teamId), event);
        }
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
