package Runner;

import Alerts.AlertGenerator;
import Data.Team;
import Persistance.S3MatchInfoDatastore;

import java.util.ArrayList;

public class GamedayRunner extends CommonRunner {

    private boolean _printOnly = false;

    public GamedayRunner() {
        super();
    }

    public GamedayRunner(ArrayList<Integer> teamIds) {
        super(teamIds);
    }

    public void RunImpl(int teamId) {
        AlertGenerator alertGen = new AlertGenerator(teamId, _printOnly);
        alertGen.Generate(_thisMatchInfo, _prevThisMatchInfo);

        for (Team team : _thisMatchInfo.teams.values()) {
            System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                               team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
        }

        ArrayList<Team> teams = new ArrayList<Team>();
        for (Team t : _thisMatchInfo.teams.values()) {
            teams.add(t);
        }
        new S3MatchInfoDatastore(teamId).WriteCurrent(_thisMatchInfo);

        System.out.format("%d (%d) - %d (%d)\n\n",
                    teams.get(0).currentPoints.startingScore,
                    teams.get(0).currentPoints.subScore,
                    teams.get(1).currentPoints.startingScore,
                    teams.get(1).currentPoints.subScore);
        System.out.println();
    }
}
