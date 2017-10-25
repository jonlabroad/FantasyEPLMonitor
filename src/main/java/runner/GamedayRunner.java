package runner;

import alerts.AlertGenerator;
import alerts.AndroidAlertSender;
import alerts.MatchEventGenerator;
import data.MatchEvent;
import data.MatchInfo;
import data.Team;
import persistance.S3MatchInfoDatastore;

import java.util.ArrayList;
import java.util.HashMap;

public class GamedayRunner extends CommonRunner {
    protected MatchInfo _thisMatchInfo = null;
    protected MatchInfo _prevThisMatchInfo = null;
    private boolean _printOnly = false;

    public GamedayRunner() {
        super();
    }

    public void runImpl(int teamId) {
        preRunTasks(teamId);

        MatchEventGenerator matchEventGen = new MatchEventGenerator(teamId, _printOnly);
        matchEventGen.Generate(_thisMatchInfo, _prevThisMatchInfo);

        AlertGenerator alertGen = new AlertGenerator(teamId, new AndroidAlertSender());
        alertGen.generateAlerts(_thisMatchInfo, _prevThisMatchInfo);

        for (Team team : _thisMatchInfo.teams.values()) {
            new S3MatchInfoDatastore().writeCurrent(team.id, _thisMatchInfo);
            System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                               team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
        }

        HashMap<Integer, Team> teams = _thisMatchInfo.teams;
        System.out.format("%d (%d) - %d (%d)\n\n",
                    teams.get(_thisMatchInfo.teamIds.get(0)).currentPoints.startingScore,
                    teams.get(_thisMatchInfo.teamIds.get(0)).currentPoints.subScore,
                    teams.get(_thisMatchInfo.teamIds.get(1)).currentPoints.startingScore,
                    teams.get(_thisMatchInfo.teamIds.get(1)).currentPoints.subScore);
        System.out.println();
    }

    private void preRunTasks(int teamId) {
        try {
            _thisMatchInfo = _matchInfoProvider.getCurrentMatch(teamId);
            if (!_forceUpdate) {
                _prevThisMatchInfo = _matchInfoDatastore.readMatchInfo(teamId, _thisMatchInfo.match.event);

                // Update this match info with previous events. This should probably be done elsewhere
                if (_prevThisMatchInfo != null) {
                    ArrayList<MatchEvent> thisMatchEvents = _thisMatchInfo.matchEvents;
                    _thisMatchInfo.matchEvents = new ArrayList<>();
                    for (MatchEvent event : _prevThisMatchInfo.matchEvents) {
                        _thisMatchInfo.matchEvents.add(event);
                    }
                    for (MatchEvent event : thisMatchEvents) {
                        _thisMatchInfo.matchEvents.add(event);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
