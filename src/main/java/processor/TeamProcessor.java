package processor;

import client.EPLClient;
import client.EPLClientFactory;
import client.MatchInfoProvider;
import data.*;
import data.eplapi.Match;
import data.eplapi.Standing;
import data.eplapi.Standings;
import persistance.S3JsonWriter;
import processor.player.ProcessedPlayerProvider;
import processor.team.MatchEventDeduplicator;
import processor.team.SingleTeamProcessor;

import java.util.*;

public class TeamProcessor {
    private EPLClient _client;

    private List<Integer> _teams;
    private int _leagueId;

    private boolean _isApiRequest = false;

    private Map<Integer, ProcessedTeam> _teamsProcessed = new HashMap<>();

    public TeamProcessor(List<Integer> teams, int leagueId, boolean apiRequest) {
        _teams = teams;
        _leagueId = leagueId;
        _isApiRequest = apiRequest;

        _client = EPLClientFactory.createClient();
    }

    public TeamProcessor(List<Integer> teams, int leagueId) {
        _teams = teams;
        _leagueId = leagueId;

        _client = EPLClientFactory.createClient();
    }

    public Map<Integer, ProcessedTeam> process() {
        Standings standings = _client.getStandings(_leagueId);

        new S3JsonWriter().write(String.format("data/%d/api/leagues-h2h-standings", _leagueId), standings, true);

        if (_teams.isEmpty()) {
            _teams = getTeamsInLeague(standings);
        }

        for (int teamId : _teams) {
            Match match = _client.findMatch(standings, teamId, false);
            int otherTeamId = teamId == match.entry_1_entry ? match.entry_2_entry : match.entry_1_entry;

            // Process both teams in the match
            ArrayList<Integer> teamIdsToProcess = new ArrayList<>();
            teamIdsToProcess.add(teamId);
            teamIdsToProcess.add(otherTeamId);
            processTeams(teamIdsToProcess, standings, match);

            ProcessedTeam team1 = _teamsProcessed.get(teamId);
            ProcessedTeam team2 = _teamsProcessed.get(otherTeamId);
            new MatchEventDeduplicator().deduplicate(team1, team2);
            if (!_isApiRequest) {
                writeMatchInfo(match);
            }
        }

        if (!_isApiRequest) {
            new AlertProcessor(_leagueId, new HashSet<>()).process();
        }
        return _teamsProcessed;
    }

    public void processTeams(Collection<Integer> teamIds, Standings standings, Match match) {
        ProcessedPlayerProvider playerProvider = new ProcessedPlayerProvider();
        for (int teamToProcess : teamIds) {
            if (!_teamsProcessed.containsKey(teamToProcess)) {
                System.out.format("Processing team %d\n", teamToProcess);
                SingleTeamProcessor processor = new SingleTeamProcessor(playerProvider, teamToProcess, _leagueId, standings, match, _client);
                ProcessedTeam processedTeam = processor.process();
                _teamsProcessed.put(teamToProcess, processedTeam);
            }
        }
    }

    private MatchInfo createMatchInfo(Match match) {
        return new MatchInfo(match.event, _teamsProcessed.get(match.entry_1_entry), _teamsProcessed.get(match.entry_2_entry));
    }

    private void writeMatchInfo(Match match) {
        MatchInfo info = createMatchInfo(match);
        info.mergeEvents();
        for (int id : info.teams.keySet()) {
            System.out.format("Writing data for %d: %s\n", id, _teamsProcessed.get(id).standing.entry_name);
            new MatchInfoProvider(_leagueId).writeCurrent(id, info);
        }
    }

    private List<Integer> getTeamsInLeague(Standings standings) {
        List<Integer> teams = new ArrayList<>();
        for (Standing standing : standings.standings.results) {
            if (standing.entry > 0) {
                teams.add(standing.entry);
            }
        }
        return teams;
    }
}
