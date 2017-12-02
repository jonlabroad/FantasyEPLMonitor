package runner;

import alerts.AlertGenerator;
import alerts.AndroidAlertSender;
import alerts.MatchEventGenerator;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import data.MatchEvent;
import data.LegacyMatchInfo;
import data.Team;
import data.TeamIdName;
import persistance.S3MatchInfoDatastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GamedayRunner extends CommonRunner {
    protected LegacyMatchInfo _thisLegacyMatchInfo = null;
    protected LegacyMatchInfo _prevThisLegacyMatchInfo = null;
    private boolean _printOnly = false;

    public GamedayRunner() {
        super();
    }

    public GamedayRunner(Collection<Integer> teamIds) {
        super(teamIds);
    }

    public void runImpl(int teamId) {
        preRunTasks(teamId);

        MatchEventGenerator matchEventGen = new MatchEventGenerator(teamId, _client, _printOnly);
        matchEventGen.Generate(_thisLegacyMatchInfo, _prevThisLegacyMatchInfo);

        AlertGenerator alertGen = new AlertGenerator(teamId, new AndroidAlertSender());
        alertGen.generateAlerts(_thisLegacyMatchInfo, _prevThisLegacyMatchInfo);

        for (Team team : _thisLegacyMatchInfo.teams.values()) {
            new S3MatchInfoDatastore(_leagueId).writeCurrent(team.id, _thisLegacyMatchInfo);
            System.out.format("#%d %s (%s) %dW-%dD-%dL\n", team.standing.rank, team.name, team.playerName,
                               team.standing.matches_won, team.standing.matches_drawn, team.standing.matches_lost);
        }

        HashMap<Integer, Team> teams = _thisLegacyMatchInfo.teams;
        System.out.format("%d (%d) - %d (%d)\n\n",
                    teams.get(_thisLegacyMatchInfo.teamIds.get(0)).currentPoints.startingScore,
                    teams.get(_thisLegacyMatchInfo.teamIds.get(0)).currentPoints.subScore,
                    teams.get(_thisLegacyMatchInfo.teamIds.get(1)).currentPoints.startingScore,
                    teams.get(_thisLegacyMatchInfo.teamIds.get(1)).currentPoints.subScore);
        System.out.println();

        boolean writeConfig = false;
        if (!GlobalConfig.CloudAppConfig.AvailableTeams.containsKey(teamId)) {
            TeamIdName teamIdName = new TeamIdName();
            teamIdName.teamId = teamId;
            teamIdName.teamName = _thisLegacyMatchInfo.teams.get(teamId).name;
            teamIdName.teamOwner = _thisLegacyMatchInfo.teams.get(teamId).playerName;
            GlobalConfig.CloudAppConfig.AvailableTeams.put(teamId, teamIdName);
            writeConfig = true;
        }

        if (GlobalConfig.CloudAppConfig.CurrentGameWeek != _thisLegacyMatchInfo.match.event) {
            GlobalConfig.CloudAppConfig.CurrentGameWeek = _thisLegacyMatchInfo.match.event;
            writeConfig = true;
        }

        if (writeConfig) {
            new CloudAppConfigProvider().write(GlobalConfig.CloudAppConfig);
        }
    }

    private void preRunTasks(int teamId) {
        try {
            _thisLegacyMatchInfo = _matchInfoProvider.getCurrentMatch(teamId);
            if (!_forceUpdate) {
                _prevThisLegacyMatchInfo = _matchInfoDatastore.readMatchInfo(teamId, _thisLegacyMatchInfo.match.event);

                // Update this match info with previous events. This should probably be done elsewhere
                if (_prevThisLegacyMatchInfo != null) {
                    ArrayList<MatchEvent> thisMatchEvents = _thisLegacyMatchInfo.matchEvents;
                    _thisLegacyMatchInfo.matchEvents = new ArrayList<>();
                    for (MatchEvent event : _prevThisLegacyMatchInfo.matchEvents) {
                        _thisLegacyMatchInfo.matchEvents.add(event);
                    }
                    for (MatchEvent event : thisMatchEvents) {
                        _thisLegacyMatchInfo.matchEvents.add(event);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
