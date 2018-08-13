package lambda;

import client.EPLClient;
import client.EPLClientFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import config.GlobalConfig;
import processor.AlertProcessor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AlertProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        int leagueId = 31187;

        Set<Integer> teamIds = new HashSet<>();
        if (GlobalConfig.TestMode) {
            teamIds.add(2365803);
        }
        new AlertProcessor(leagueId, teamIds, EPLClientFactory.createClient()).process();
        return null;
    }
}
