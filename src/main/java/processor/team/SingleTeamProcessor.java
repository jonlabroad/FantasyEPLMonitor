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
    int _gameweek;
    EPLClient _client;
    ProcessedPlayerProvider _playerProvider;
    ProcessedTeam _processedTeam = null;

    public SingleTeamProcessor(ProcessedPlayerProvider provider, int teamId, int gameweek, EPLClient client) {
        _teamId = teamId;
        _gameweek = gameweek;

        _playerProvider = provider;
        _client = client;
    }

    public void process() {
        // Collect the player information
        ArrayList<ProcessedPick> processedPicks = getPlayersForTeam();

        // Calculate score
        Score score = new ScoreCalculator().calculate(processedPicks);

        // Merge all the events into a single stream
        List<TeamMatchEvent> events = mergeEvents(processedPicks);
        EntryData entry = _client.getEntry(_teamId);
        Picks picks = _client.getPicks(_teamId, _gameweek);
        ProcessedTeam team = new ProcessedTeam(_teamId, entry, processedPicks, score, events, picks.active_chip);
        List<TeamMatchEvent> autosubs = new AutosubDetector().detectAutoSubs(_teamId, null, team.picks);
        team.setAutosubs(autosubs);

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
}
