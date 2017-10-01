import Alerts.AlertGenerator;
import Alerts.MatchInfoComparer;
import Alerts.MatchInfoDifference;
import Alerts.SMSAlertSender;
import Client.EPLClient;
import Config.GlobalConfig;
import Config.SecretConfig;
import Config.SecretConfigurator;
import Config.User;
import Data.EPLAPI.Footballer;
import Data.MatchInfo;
import Data.Team;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {

        boolean forceUpdate = true;

        ArrayList<Integer> teamIds = new ArrayList<Integer>();
        teamIds.add(2365803);
        //teamIds.add(1326527);
        EPLClient client = new EPLClient();
        Footballer[] footballers = client.GetFootballers();
        for (int teamId : teamIds) {
            //int teamId = 2365803;
            int leagueId = 31187;
            //while (true) {
            MatchInfo info = client.GetMatchInfo(leagueId, teamId);
            IMatchInfoDatastore datastore = new S3MatchInfoDatastore(teamId);
            MatchInfo oldInfo = null;
            if (!forceUpdate) {
                oldInfo = datastore.ReadLast(teamId);
            }
            datastore.WriteCurrent(info);

            AlertGenerator alertGen = new AlertGenerator(teamId);
            alertGen.Generate(info, oldInfo, footballers);

            for (Team team : info.teams) {
                System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                        team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
            }

            System.out.format("%d(%d) - %d(%d)\n\n",
                    info.teams.get(0).currentPoints.startingScore,
                    info.teams.get(0).currentPoints.subScore,
                    info.teams.get(1).currentPoints.startingScore,
                    info.teams.get(1).currentPoints.subScore);
            System.out.println();
            //    Thread.sleep(20000);
            //}
        }
    }
}
