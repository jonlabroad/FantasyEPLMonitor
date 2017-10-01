package Lambda;

import Alerts.AlertGenerator;
import Client.EPLClient;
import Data.EPLAPI.Footballer;
import Data.MatchInfo;
import Data.Team;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DataCollectionAndAlerting implements RequestHandler<Map<String, Object>, Void>{
    public Void handleRequest(Map<String, Object> params, Context context) {
        ArrayList<Integer> teamIds = new ArrayList<Integer>();
        teamIds.add(2365803);
        teamIds.add(1326527);
        EPLClient client = null;
        Footballer[] footballers = null;
        try {
            client = new EPLClient();
            footballers = client.GetFootballers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        for (int teamId : teamIds) {
            try {
                //int teamId = 2365803;
                //if (params.containsKey("teamId")) {
                //    teamId = (Integer) params.get("teamId");
                //}
                int leagueId = 31187;

                MatchInfo info = client.GetMatchInfo(leagueId, teamId);
                IMatchInfoDatastore datastore = new S3MatchInfoDatastore(teamId);
                MatchInfo oldInfo = datastore.ReadLast(teamId);
                datastore.WriteCurrent(info);

                AlertGenerator alertGen = new AlertGenerator(teamId);
                alertGen.Generate(info, oldInfo, footballers);

                for (Team team : info.teams) {
                    System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                            team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
                }

                System.out.format("%d (%d) - %d (%d)\n\n",
                        info.teams.get(0).currentPoints.startingScore,
                        info.teams.get(0).currentPoints.subScore,
                        info.teams.get(1).currentPoints.startingScore,
                        info.teams.get(1).currentPoints.subScore);
                System.out.println();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }
}
