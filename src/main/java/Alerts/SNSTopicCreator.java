package Alerts;

import Config.GlobalConfig;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.Topic;

public class SNSTopicCreator {
    AmazonSNS _sns = AmazonSNSClientBuilder.defaultClient();

    public void CreateTopic(int teamId) {
        String topicName = CreateTopicName(teamId);
        try {
            if (TopicExists(topicName)) {
                _sns.createTopic(topicName);
            }
        }
        catch (Exception ex) {
            System.out.format("Unable to create topic named %s: %s\n", topicName, ex.getMessage());
        }
    }

    private boolean TopicExists(String topicName) {
        ListTopicsResult topicList = _sns.listTopics();
        for (Topic topic : topicList.getTopics()) {
            if (topic.getTopicArn().endsWith(topicName)) {
                return true;
            }
        }
        return false;
    }

    private String CreateTopicName(int teamId) {
        return String.format(GlobalConfig.TopicArnFormat, teamId);
    }
}
