package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import processor.TeamProcessor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TeamProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        Set<Integer> teams = new HashSet<>();
        teams.add(1326527);
        teams.add(2365803);

        int leagueId = 31187;

        TeamProcessor processor = new TeamProcessor(teams, leagueId);
        processor.process();
        return null;
    }
}
