package processor.team;

import client.EPLClient;
import client.ScoreCalculator;
import config.GlobalConfig;
import data.*;
import data.eplapi.*;
import processor.player.PlayerReader;

import java.util.ArrayList;
import java.util.List;

public class SingleTeamProcessor {
    private int _teamId;
    private int _leagueId;
    Standings _standings;
    Match _match;
    EPLClient _client;

    PlayerReader _playerReader;

    public SingleTeamProcessor(int teamId, int leagueId, Standings standings, Match match, EPLClient client) {
        _teamId = teamId;
        _leagueId = leagueId;
        _standings = standings;
        _match = match;

        _playerReader = new PlayerReader();
        _client = client;
    }

    public ProcessedTeam process() {
        // Collect the player information
        ArrayList<ProcessedPick> processedPicks = getPlayersForTeam();

        // Calculate score
        Score score = new ScoreCalculator().calculate(processedPicks);

        // Merge all the events into a single stream
        List<TeamMatchEvent> events = mergeEvents(processedPicks);
        ProcessedTeam team = new ProcessedTeam(_teamId, findStanding(_standings), processedPicks, score, events);
        List<TeamMatchEvent> autosubs = new AutosubDetector().detectAutoSubs(_teamId, null, team.picks);
        team.setAutosubs(autosubs);

        return team;
    }

    public ArrayList<ProcessedPick> getPlayersForTeam() {
        ArrayList<ProcessedPick> processedPicks = new ArrayList<>();
        Picks picks = _client.getPicks(_teamId, _match.event);
        if (picks == null) {
            return processedPicks;
        }

        for (Pick pick : picks.picks) {
            ProcessedPlayer processedPlayer = readProcessedPlayer(pick.element);
            ProcessedPick processedPick = new ProcessedPick(processedPlayer, pick);
            processedPicks.add(processedPick);
        }
        return processedPicks;
    }

    List<TeamMatchEvent> mergeEvents(ArrayList<ProcessedPick> picks) {
        List<TeamMatchEvent> events = new ArrayList<>();
        for (ProcessedPick pick : picks) {
            for (MatchEvent event : pick.footballer.events) {
                TeamMatchEvent tEvent = new TeamMatchEvent(_teamId, pick.isCaptain(), pick.getMultiplier(), event);
                events.add(tEvent);
            }
        }
        return events;
    }

    private ProcessedPlayer readProcessedPlayer(int footballerId) {
        return _playerReader.read(GlobalConfig.CloudAppConfig.CurrentGameWeek, footballerId);
    }

    private Standing findStanding(Standings standings) {
        for (Standing standing : standings.standings.results) {
            if (standing.entry == _teamId) {
                return standing;
            }
        }
        return null;
    }
}
