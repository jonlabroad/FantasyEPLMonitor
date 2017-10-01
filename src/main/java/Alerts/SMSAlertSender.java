package Alerts;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class SMSAlertSender implements IAlertSender {
    private static final String TOPIC_ARN_FORMAT = "arn:aws:sns:us-east-1:796987500533:matchtrackeralert_%d";
    private String _phoneNumber;
    private AmazonSNS _client;

    public SMSAlertSender(String phoneNumber) {
        _phoneNumber = phoneNumber;
        _client = AmazonSNSClientBuilder.defaultClient();
    }

    public void SendAlert(int teamId, String text) {
        System.out.format("Sending alert for team %d\n", teamId);
        _client.publish(GetTopicName(teamId), text);
    }

    private String GetTopicName(int teamId) {
        return String.format(TOPIC_ARN_FORMAT, teamId);
    }
}
