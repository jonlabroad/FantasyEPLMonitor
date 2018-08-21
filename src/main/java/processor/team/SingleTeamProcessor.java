package processor.team;

import client.EPLClient;
import client.ScoreCalculator;
import data.*;
import data.eplapi.*;
import processor.player.ProcessedPlayerProvider;
import util.IParallelizableProcess;

import java.util.ArrayList;
import java.util.List;

public class SingleTeamProcessor implements IParallelizableProcess {
    private int _teamId;
    int _leagueId;
    int _gameweek;
    EPLClient _client;
    ProcessedPlayerProvider _playerProvider;
    ProcessedTeam _processedTeam = null;

    public SingleTeamProcessor(ProcessedPlayerProvider provider, int teamId, int gameweek, int leagueId, EPLClient client) {
        _teamId = teamId;
        _gameweek = gameweek;
        _leagueId = leagueId;

        _playerProvider = provider;
        _client = client;
    }

    public void process() {
        // Collect the player information
        ArrayList<ProcessedPick> processedPicks = getPlayersForTeam();

        Picks picks = _client.getPicks(_teamId, _gameweek);
        Event event = getCurrentEvent();
        boolean useEventScore = event.data_checked && event.finished;

        // Calculate score
        Score score;
        if (!useEventScore) {
            score = new ScoreCalculator().calculate(picks, processedPicks, picks != null && picks.active_chip.equals("bboost"));
        }
        else {
            score = new Score();
            if (picks != null) {
                score.startingScore = picks.entry_history.points;
                score.subScore = picks.entry_history.points_on_bench;
            }
            else {
                // AVERAGE
                Standings standings = _client.getStandings(_leagueId);
                for (Match match : standings.matches_this.results) {
                    if (match.entry_1_entry == 0) {
                        score.startingScore = match.entry_1_points;
                        break;
                    }
                    else if (match.entry_2_entry == 0) {
                        score.startingScore = match.entry_2_points;
                        break;
                    }
                }
            }
        }

        // Merge all the events into a single stream
        List<TeamMatchEvent> events = mergeEvents(processedPicks);
        EntryData entry = _client.getEntry(_teamId);
        ProcessedTeam team = new ProcessedTeam(_teamId, entry, processedPicks, score, events, picks != null ? picks.active_chip : "");
        List<TeamMatchEvent> autosubs = new AutosubDetector().detectAutoSubs(_teamId, null, team.picks);
        team.setAutosubs(autosubs);
        team.transferCost = picks != null ? picks.entry_history.event_transfers_cost : 0;

        _processedTeam = team;
    }

    public ProcessedTeam getResult() {
        return _processedTeam;
    }

    public int getId() {
        return _teamId;
    }

    public ArrayList<ProcessedPick> getPlayersForTeam() {
        ArrayList<ProcessedPick> processedPicks = new ArrayList<>();
        Picks picks = _client.getPicks(_teamId, _gameweek);
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
        return _playerProvider.getPlayer(footballerId);
    }

    private Event getCurrentEvent() {
        BootstrapStatic boot = _client.getBootstrapStatic();
        int currentEvent = boot.currentEvent;
        for (Event event : boot.events) {
            if (event.id == currentEvent) {
                return event;
            }
        }
        return null;
    }
}
