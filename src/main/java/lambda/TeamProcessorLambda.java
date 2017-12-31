package lambda;

import client.EPLClientFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import config.GlobalConfig;
import data.ProcessedTeam;
import dispatcher.TeamProcessorDispatcher;

import java.util.*;

public class TeamProcessorLambda implements RequestHandler<Map<String, Object>, String> {
    public String handleRequest(Map<String, Object> params, Context context) {
        List<Integer> teams = readTeamIds(params);
        if (teams == null) {
            teams = new ArrayList<>();
            if (!GlobalConfig.TestMode) {
                //teams.add(1326527);
                //teams.add(2365803);
                //teams.add(3303381);
            }
            else {
                teams.add(2365803);
            }
        }

        int leagueId = 31187;

        TeamProcessorDispatcher processor = new TeamProcessorDispatcher(EPLClientFactory.createClient(), teams, leagueId);
        processor.start();
        Map<Integer, ProcessedTeam> processedTeams = processor.join();
        return new Gson().toJson(processedTeams);
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
