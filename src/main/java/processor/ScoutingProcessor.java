package processor;

import client.EPLClient;
import client.EPLClientFactory;
import config.GlobalConfig;
import data.*;
import data.eplapi.Match;
import data.eplapi.Standing;
import data.eplapi.Standings;
import persistance.S3JsonWriter;
import processor.scouting.H2hSimulator;
import processor.scouting.Record;
import processor.team.DifferentialFinder;
import processor.team.MatchEventDeduplicator;

import java.util.*;

public class ScoutingProcessor {
    Set<Integer> _processedTeams = new HashSet<>();
    Map<Integer, ProcessedTeam> _teams = new HashMap<>();
    int _leagueId;
    EPLClient _client = null;

    public ScoutingProcessor(int leagueId, EPLClient client, Map<Integer, ProcessedTeam> teams) {
        initialize(leagueId, client, teams);
    }

    protected void initialize(int leagueId, EPLClient client, Map<Integer, ProcessedTeam> teams) {
        if (client == null) {
            client = EPLClientFactory.createClient();
        }
        _client = client;
        _teams = teams;
        _leagueId = leagueId;
    }

    public HashMap<Integer, ScoutingReport> process() {
        HashMap<Integer, ScoutingReport> reports = new HashMap<>();
        Standings standings = _client.getStandings(_leagueId);
        for (int gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek; gameweek <= 38; gameweek++) {
            for (ProcessedTeam team : _teams.values()) {
                if (_processedTeams.contains(team.id)) {
                    System.out.format("Team scouting report already processed (gw=%d): %d\n", gameweek, team.id);
                    continue;
                }

                ScoutingReport report = new ScoutingReport();
                report.gameweek = gameweek;
                findMatch(team.id, report, gameweek);

                processTeams(_teams, report.match, report, standings);

                findDifferential(report);
                generateStats(report);
                simulateH2h(report);
                writeReports(report, gameweek);

                reports.put(report.match.entry_1_entry, report);
                reports.put(report.match.entry_2_entry, report);
                _processedTeams.add(report.match.entry_1_entry);
                _processedTeams.add(report.match.entry_2_entry);
            }
            _processedTeams.clear();
        }

        return reports;
    }

    private void generateStats(ScoutingReport report)
    {
        for (ProcessedTeam team : report.teams.values())
        {
            addBestPlayer(team, report);
            addInformPlayer(team, report);
        }
    }

    private TeamStats getStats(ProcessedTeam team, ScoutingReport report) {
        if (!report.stats.containsKey(team.id)) {
            report.stats.put(team.id, new TeamStats());
        }
        return report.stats.get(team.id);
    }

    private void addBestPlayer(ProcessedTeam team, ScoutingReport report)
    {
        ProcessedPlayer player = findBestPlayer(team);
        TeamStats stats = getStats(team, report);
        stats.bestPlayer = player;
    }

    private void addInformPlayer(ProcessedTeam team, ScoutingReport report)
    {
        ProcessedPlayer player = findInformPlayer(team);
        TeamStats stats = getStats(team, report);
        stats.informPlayer = player;
    }

    private ProcessedPlayer findBestPlayer(ProcessedTeam team)
    {
        double maxPts = 0.0;
        ProcessedPlayer bestPlayer = null;
        for (ProcessedPick pick : team.picks)
        {
            try {
                double pointsPerGame = Double.parseDouble(pick.footballer.rawData.footballer.points_per_game);
                if (pointsPerGame > 0.0 && pointsPerGame > maxPts) {
                    maxPts = pointsPerGame;
                    bestPlayer = pick.footballer;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return bestPlayer;
    }

    private ProcessedPlayer findInformPlayer(ProcessedTeam team)
    {
        double maxPts = 0.0;
        ProcessedPlayer bestPlayer = null;
        for (ProcessedPick pick : team.picks)
        {
            try {
                double form = Double.parseDouble(pick.footballer.rawData.footballer.form);
                if (form > 0.0 && form > maxPts) {
                    maxPts = form;
                    bestPlayer = pick.footballer;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return bestPlayer;
    }

    private void writeReports(ScoutingReport report, int gameweek) {
        S3JsonWriter writer = new S3JsonWriter();
        for (ProcessedTeam team : report.teams.values()) {
            if (team == null) {
                continue;
            }
            writer.write(String.format(GlobalConfig.DataRoot + "/%d/%d/%d/ScoutingReport", _leagueId, team.id, gameweek), report, true);
        }
    }

    private void simulateH2h(ScoutingReport report) {
        if (report.teams.containsValue(null)) {
            return;
        }
        H2hSimulator simulator = new H2hSimulator(_client, report.match.entry_1_entry, report.match.entry_2_entry);
        report.simulatedH2h = simulator.simulate();
    }

    private void findMatch(int teamId, ScoutingReport report, int gameweek) {
        Match match = findMatch(teamId, gameweek);
        report.match = match;
    }

    private Match findMatch(int teamId, int gameweek) {
        Match match = _client.findMatch(_leagueId, teamId, gameweek);
        if (match == null) {
            System.out.format("Unable to find match for team %d, gameweek %d\n", teamId, gameweek);
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

    protected Standing getStanding(Standings standings, int teamId) {
        if (standings == null) {
            return null;
        }

        for (Standing standing : standings.standings.results) {
            if (standing.entry == teamId) {
                return standing;
            }
        }
        return null;
    }

    private void processTeams(Map<Integer, ProcessedTeam> teams, Match match, ScoutingReport report, Standings standings) {
        HashSet<Integer> teamIds = new HashSet<>();
        teamIds.add(match.entry_1_entry);
        teamIds.add(match.entry_2_entry);
        for (int teamId : teamIds) {
            ProcessedTeam team = teams.get(teamId);
            ProcessedMatchTeam matchTeam = new ProcessedMatchTeam(team, getStanding(standings, team.id));
            report.teams.put(teamId, matchTeam);
        }
    }
}
