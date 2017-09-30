import Client.EPLClient;
import Data.MatchInfo;
import Data.Team;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        int teamId = 2365803;
        int leagueId = 31187;
        EPLClient client = new EPLClient();

        while (true) {
            MatchInfo info = client.GetMatchInfo(leagueId, teamId);
            for (Team team : info.teams) {
                System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
            }
            System.out.format("%d - %d\n\n", info.teams.get(0).currentPoints, info.teams.get(1).currentPoints);
            System.out.println();
            Thread.sleep(20000);
        }
    }
}
