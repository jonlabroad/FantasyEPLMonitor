package processor;

import client.EPLClient;
import client.EPLClientFactory;
import client.MatchInfoProvider;
import data.*;
import data.eplapi.Match;
import data.eplapi.Pick;
import data.eplapi.Standings;
import persistance.S3MatchInfoDatastore;
import processor.team.SingleTeamProcessor;

import java.util.*;

public class TeamProcessor {
    private EPLClient _client;

    private Collection<Integer> _teams;
    private int _leagueId;

    private Map<Integer, ProcessedTeam> _teamsProcessed = new HashMap<>();

    public TeamProcessor(Collection<Integer> teams, int leagueId) {
        _teams = teams;
        _leagueId = leagueId;

        _client = EPLClientFactory.createClient();
    }

    public void process() {
        Standings standings = _client.getStandings(_leagueId);

        for (int teamId : _teams) {
            Match match = _client.findMatch(standings, teamId, false);
            int otherTeamId = teamId == match.entry_1_entry ? match.entry_2_entry : match.entry_1_entry;

            // Process both teams in the match
            ArrayList<Integer> teamIdsToProcess = new ArrayList<>();
            teamIdsToProcess.add(teamId);
            teamIdsToProcess.add(otherTeamId);
            processTeams(teamIdsToProcess, standings, match);

            writeLegacyMatchInfo(teamIdsToProcess, match);
            writeMatchInfo(match);
        }
    }

    public void processTeams(Collection<Integer> teamIds, Standings standings, Match match) {
        for (int teamToProcess : teamIds) {
            if (!_teamsProcessed.containsKey(teamToProcess)) {
                System.out.format("Processing team %d\n", teamToProcess);
                SingleTeamProcessor processor = new SingleTeamProcessor(teamToProcess, _leagueId, standings, match, _client);
                ProcessedTeam processedTeam = processor.process();
                _teamsProcessed.put(teamToProcess, processedTeam);
            }
        }
    }

    private MatchInfo createMatchInfo(Match match) {
        return new MatchInfo(match.event, _teamsProcessed.get(match.entry_1_entry), _teamsProcessed.get(match.entry_2_entry));
    }

    private LegacyMatchInfo createLegacyMatchInfo(ArrayList<Integer> teamIds, Match match) {
        ProcessedTeam team1 = _teamsProcessed.get(teamIds.get(0));
        ProcessedTeam team2 = _teamsProcessed.get(teamIds.get(1));

        LegacyMatchInfo matchInfo = new LegacyMatchInfo();
        matchInfo.match = match;
        matchInfo.matchEvents.addAll(team1.events);
        matchInfo.matchEvents.addAll(team2.events);
        matchInfo.teamIds = teamIds;
        matchInfo.teams.put(team1.id, createLegacyTeam(team1));
        matchInfo.teams.put(team2.id, createLegacyTeam(team2));
        return matchInfo;
    }

    private Team createLegacyTeam(ProcessedTeam team) {
        Team legacyTeam = new Team();
        legacyTeam.id = team.id;
        legacyTeam.playerName = team.standing.player_name;
        legacyTeam.standing = team.standing;
        legacyTeam.name = team.standing.entry_name;
        legacyTeam.currentPoints = team.score;
        legacyTeam.picks.picks = new Pick[team.picks.size()];
        for (int i = 0; i < team.picks.size(); i++) {
            ProcessedPick processedPick = team.picks.get(i);
            legacyTeam.picks.picks[i] = processedPick.pick;
            legacyTeam.footballerDetails.put(processedPick.footballer.rawData.footballer.id, processedPick.footballer.rawData.details);
        }
        return legacyTeam;
    }

    private void writeLegacyMatchInfo(ArrayList<Integer> teamIds, Match match) {
        LegacyMatchInfo legacyMatchInfo = createLegacyMatchInfo(teamIds, match);
        for (int id : teamIds) {
            System.out.format("Writing legacy match info for %d\n", id);
            new S3MatchInfoDatastore(_leagueId).writeCurrent(id, legacyMatchInfo);
        }
    }

    private void writeMatchInfo(Match match) {
        MatchInfo info = createMatchInfo(match);
        for (int id : info.teams.keySet()) {
            new MatchInfoProvider(_leagueId).writeCurrent(id, info);
        }
    }
}
