package processor;

import client.EPLClient;
import client.EPLClientFactory;
import config.GlobalConfig;
import data.ProcessedTeam;
import data.ScoutingReport;
import data.eplapi.Match;
import data.eplapi.Standings;
import persistance.S3JsonWriter;
import processor.scouting.H2hSimulator;
import processor.scouting.Record;
import processor.team.DifferentialFinder;
import processor.team.SingleTeamProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ScoutingProcessor {
/*
    int _teamId;
    int _gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek + 1;
    int _leagueId;
    EPLClient _client = null;

    public ScoutingProcessor(int leagueId, int teamId) {
        initialize(leagueId, null, GlobalConfig.CloudAppConfig.CurrentGameWeek + 1, teamId);
    }

    protected void initialize(int leagueId, EPLClient client, int gameweek, int teamId) {
        if (client == null) {
            client = EPLClientFactory.createClient();
        }
        _client = client;
        _gameweek = gameweek;
        _teamId = teamId;
        _leagueId = leagueId;
    }

    public ScoutingReport process() {
        ScoutingReport report = new ScoutingReport();
        report.gameweek = _gameweek;
        findNextMatch(report);
        processTeams(report.match, report);
        findDifferential(report);
        simulateH2h(report);
        writeReports(report);

        return report;
    }

    private void writeReports(ScoutingReport report) {
        S3JsonWriter writer = new S3JsonWriter();
        for (ProcessedTeam team : report.teams.values()) {
            writer.write(String.format("data/%d/%d/%d/ScoutingReport", _leagueId, team.id, _gameweek), report, true);
        }
    }

    private void simulateH2h(ScoutingReport report) {
        ArrayList<ProcessedTeam> teams = new ArrayList<>();
        for (ProcessedTeam team : report.teams.values()) {
            teams.add(team);
        }
        H2hSimulator simulator = new H2hSimulator(_client, teams.get(0).id, teams.get(1).id);
        report.simulatedH2h = simulator.simulate();
    }

    private Match findPreviousMatch(int teamId) {
        return findMatch(_gameweek - 1);
    }

    private void findNextMatch(ScoutingReport report) {
        Match nextMatch = findMatch(_gameweek);
        report.match = nextMatch;
    }

    private Match findMatch(int gameweek) {
        Match match = _client.findMatch(_leagueId, _teamId, gameweek);
        if (match == null) {
            System.out.format("Unable to find match for team %d, gameweek %d\n", _teamId, _gameweek);
        }
        return match;
    }

    private void findDifferential(ScoutingReport report) {
        ArrayList<ProcessedTeam> teams = new ArrayList<>();
        for (ProcessedTeam team : report.teams.values()) {
            teams.add(team);
        }

        DifferentialFinder diffFinder = new DifferentialFinder(teams.get(0), teams.get(1));
        report.differentials = diffFinder.find();
    }

    private void processTeams(Match match, ScoutingReport report) {
        HashSet<Integer> teamIds = new HashSet<>();
        teamIds.add(match.entry_1_entry);
        teamIds.add(match.entry_2_entry);
        for (int teamId : teamIds) {
            Match prevMatch = findPreviousMatch(teamId);
            ProcessedTeam team = processTeam(teamId, prevMatch);
            report.teams.put(teamId, team);
        }
    }

    private ProcessedTeam processTeam(int teamId, Match match) {
        ArrayList<Integer> teams = new ArrayList<>();
        teams.add(teamId);

        Standings standings = _client.getStandings(_leagueId);
        SingleTeamProcessor processor = new SingleTeamProcessor(teams, _leagueId);
        Map<Integer, ProcessedTeam> processedTeams = processor.processTeams(teams, standings, match);
        for (ProcessedTeam pTeam : processedTeams.values()) {
            return pTeam;
        }
        return null;
    }
    */
}
