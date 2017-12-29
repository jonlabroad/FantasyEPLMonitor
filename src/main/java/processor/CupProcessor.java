package processor;

import client.MatchInfoProvider;
import config.GlobalConfig;
import data.MatchInfo;
import data.ProcessedTeam;
import data.eplapi.*;
import processor.player.ProcessedPlayerProvider;
import processor.team.MatchEventDeduplicator;
import processor.team.SingleTeamProcessor;

import java.util.*;

public class CupProcessor extends TeamProcessor {

    public CupProcessor(List<Integer> teams, int leagueId, boolean apiRequest) {
        super(teams, leagueId, apiRequest);
    }

    @Override
    public Map<Integer, ProcessedTeam> process() {
        Standings standings = _client.getStandings(_leagueId);

        if (_teams.isEmpty()) {
            _teams = getTeamsInLeague(standings);
        }

        for (int teamId : _teams) {
            Match cup = getCurrentCupMatch(teamId);
            if (cup == null) {
                continue;
            }

            System.out.format("%s is still in the cup!\n", cup.entry_1_name);
            System.out.format("%s is still in the cup!\n", cup.entry_2_name);
            int otherTeamId = teamId == cup.entry_1_entry ? cup.entry_2_entry : cup.entry_1_entry;

            // Process both teams in the match
            ArrayList<Integer> teamIdsToProcess = new ArrayList<>();
            teamIdsToProcess.add(teamId);
            teamIdsToProcess.add(otherTeamId);
            processTeams(teamIdsToProcess, cup);

            ProcessedTeam team1 = _teamsProcessed.get(teamId);
            ProcessedTeam team2 = _teamsProcessed.get(otherTeamId);
            new MatchEventDeduplicator().deduplicate(team1, team2);
            if (!_isApiRequest) {
                writeCupInfo(cup);
            }
        }

        if (!_isApiRequest) {
            new AlertProcessor(_leagueId, new HashSet<>()).process();
        }
        return _teamsProcessed;

    }

    public void processTeams(Collection<Integer> teamIds, Match cup) {
        ProcessedPlayerProvider playerProvider = new ProcessedPlayerProvider();
        for (int teamToProcess : teamIds) {
            if (!_teamsProcessed.containsKey(teamToProcess)) {
                System.out.format("Processing team %d\n", teamToProcess);
                SingleTeamProcessor processor = new SingleTeamProcessor(playerProvider, teamToProcess, _leagueId, null, cup, _client);
                ProcessedTeam processedTeam = processor.process();
                _teamsProcessed.put(teamToProcess, processedTeam);
            }
        }
    }

    protected void writeCupInfo(Match match) {
        MatchInfo info = createMatchInfo(match);
        info.mergeEvents();
        for (int id : info.teams.keySet()) {
            System.out.format("Writing data for %d: %s\n", id, _teamsProcessed.get(id).entry.entry.name);
            new MatchInfoProvider(_leagueId).writeCup(id, info);
        }
    }

    private Match getCurrentCupMatch(int teamId) {
        int gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek;
        ArrayList<Match> cups = getCups(teamId);
        for (Match cup : cups) {
            if (cup.event == gameweek) {
                return cup;
            }
        }
        return null;
    }

    private ArrayList<Match> getCups(int teamId) {
        EntryData entry = _client.getEntry(teamId);
        return entry.leagues.cup != null ? entry.leagues.cup : new ArrayList<>();
    }
}
