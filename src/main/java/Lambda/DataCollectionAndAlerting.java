package Lambda;

import Alerts.AlertGenerator;
import Client.EPLClient;
import Data.MatchInfo;
import Data.Team;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.ArrayList;
import java.util.Map;

public class DataCollectionAndAlerting implements RequestHandler<Map<String, Object>, Void>{
    public Void handleRequest(Map<String, Object> params, Context context) {
        ArrayList<Integer> teamIds = new ArrayList<Integer>();
        teamIds.add(2365803);
        teamIds.add(1326527);
        for (int teamId : teamIds) {
            try {
                //int teamId = 2365803;
                //if (params.containsKey("teamId")) {
                //    teamId = (Integer) params.get("teamId");
                //}
                int leagueId = 31187;
                EPLClient client = new EPLClient();

                MatchInfo info = client.GetMatchInfo(leagueId, teamId);
                IMatchInfoDatastore datastore = new S3MatchInfoDatastore(teamId);
                MatchInfo oldInfo = datastore.ReadLast(teamId);
                datastore.WriteCurrent(info);

                AlertGenerator alertGen = new AlertGenerator(teamId);
                alertGen.Generate(info, oldInfo);

                for (Team team : info.teams) {
                    System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                            team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
                }

                System.out.format("%d - %d\n\n", info.teams.get(0).currentPoints, info.teams.get(1).currentPoints);
                System.out.println();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }
}
