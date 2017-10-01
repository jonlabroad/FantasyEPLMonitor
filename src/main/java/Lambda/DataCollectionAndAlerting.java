package Lambda;

import Alerts.AlertGenerator;
import Client.EPLClient;
import Data.MatchInfo;
import Data.Team;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;

public class DataCollectionAndAlerting implements RequestHandler<Integer, Void>{
    public Void handleRequest(Integer teamId, Context context) {
        try {
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
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
