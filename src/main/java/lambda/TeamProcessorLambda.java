package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import processor.TeamProcessor;

import java.util.*;

public class TeamProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        List<Integer> teams = readTeamIds(params);
        if (teams == null) {
            teams = new ArrayList<>();
            teams.add(1326527);
            teams.add(2365803);
        }

        int leagueId = 31187;

        TeamProcessor processor = new TeamProcessor(teams, leagueId);
        processor.process();
        return null;
    }

    private List<Integer> readTeamIds(Map<String, Object> params) {
        if (params.containsKey("teamIds")) {
            List<Integer> teamIds = (ArrayList<Integer>) params.get("teamIds");
            return teamIds;
        }
        else {
            System.out.println("No team Ids found in params");
        }
        return null;
    }
}
