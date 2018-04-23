package processor;

import client.EPLClient;
import client.EPLClientFactory;
import config.GlobalConfig;
import data.ProcessedTeam;
import data.ScoutingReport;
import data.eplapi.Match;
import persistance.S3JsonWriter;
import processor.scouting.H2hSimulator;
import processor.team.DifferentialFinder;

import java.util.*;

public class ScoutingProcessor {
    Set<Integer> _processedTeams = new HashSet<>();
    Map<Integer, ProcessedTeam> _teams = new HashMap<>();
    int _gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek + 1;
    int _leagueId;
    EPLClient _client = null;

    public ScoutingProcessor(int leagueId, Map<Integer, ProcessedTeam> teams) {
        initialize(leagueId, null, GlobalConfig.CloudAppConfig.CurrentGameWeek + 1, teams);
    }

    protected void initialize(int leagueId, EPLClient client, int gameweek, Map<Integer, ProcessedTeam> teams) {
        if (client == null) {
            client = EPLClientFactory.createClient();
        }
        _client = client;
        _gameweek = gameweek;
        _teams = teams;
        _leagueId = leagueId;
    }

    public HashMap<Integer, ScoutingReport> process() {
        HashMap<Integer, ScoutingReport> reports = new HashMap<>();
        for (ProcessedTeam team : _teams.values()) {
            if (_processedTeams.contains(team.id)) {
                System.out.format("Team scouting report already processed: %d\n", team.id);
                continue;
            }

            ScoutingReport report = new ScoutingReport();
            report.gameweek = _gameweek;
            findNextMatch(team.id, report);

            processTeams(_teams, report.match, report);
            findDifferential(report);
            simulateH2h(report);
            writeReports(report);

            reports.put(report.match.entry_1_entry, report);
            reports.put(report.match.entry_2_entry, report);
            _processedTeams.add(report.match.entry_1_entry);
            _processedTeams.add(report.match.entry_2_entry);
        }

        return reports;
    }

    private void writeReports(ScoutingReport report) {
        S3JsonWriter writer = new S3JsonWriter();
        for (ProcessedTeam team : report.teams.values()) {
            if (team == null) {
                continue;
            }
            writer.write(String.format("data/%d/%d/%d/ScoutingReport", _leagueId, team.id, _gameweek), report, true);
        }
    }

    private void simulateH2h(ScoutingReport report) {
        ArrayList<ProcessedTeam> teams = new ArrayList<>();
        for (ProcessedTeam team : report.teams.values()) {
            teams.add(team);
        }

        if (report.teams.containsValue(null)) {
            return;
        }
        H2hSimulator simulator = new H2hSimulator(_client, teams.get(0).id, teams.get(1).id);
        report.simulatedH2h = simulator.simulate();
    }

    private Match findPreviousMatch(int teamId) {
        return findMatch(teamId, _gameweek - 1);
    }

    private void findNextMatch(int teamId, ScoutingReport report) {
        Match nextMatch = findMatch(teamId, _gameweek);
        report.match = nextMatch;
    }

    private Match findMatch(int teamId, int gameweek) {
        Match match = _client.findMatch(_leagueId, teamId, gameweek);
        if (match == null) {
            System.out.format("Unable to find match for team %d, gameweek %d\n", teamId, _gameweek);
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

    private void processTeams(Map<Integer, ProcessedTeam> teams, Match match, ScoutingReport report) {
        HashSet<Integer> teamIds = new HashSet<>();
        teamIds.add(match.entry_1_entry);
        teamIds.add(match.entry_2_entry);
        for (int teamId : teamIds) {
            Match prevMatch = findPreviousMatch(teamId);
            ProcessedTeam team = teams.get(teamId);
            report.teams.put(teamId, team);
        }
    }
}
