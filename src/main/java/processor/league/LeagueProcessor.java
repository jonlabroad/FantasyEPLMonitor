package processor.league;

import client.EPLClient;
import client.ScoreCalculator;
import config.GlobalConfig;
import data.LiveTeamStat;
import data.LiveTeamStatComparator;
import data.ProcessedTeam;
import persistance.S3JsonWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LeagueProcessor {
    private static final String KEY_PATH_FORMAT = "%s/%d/LiveLeague_%d.json";

    private int _gameweek;
    private int _leagueId;
    private Collection<ProcessedTeam> _teams;

    public LeagueProcessor(Collection<ProcessedTeam> teams, int leagueId, int gameweek)
    {
        _teams = teams;
        _leagueId = leagueId;
        _gameweek = gameweek;
    }

    public void process()
    {
        findLiveGameweekStandings();
    }

    private void findLiveGameweekStandings()
    {
        List<LiveTeamStat> stats = new ArrayList<>();
        for (ProcessedTeam team : _teams) {
            if (team.id == 0) {
                continue;
            }

            LiveTeamStat stat = new LiveTeamStat();
            stat.teamId = team.id;
            stat.teamName = team.entry.entry != null ? team.entry.entry.name : "???";
            stat.score = team.score;
            stats.add(stat);
        }
        stats.sort(new LiveTeamStatComparator());
        new S3JsonWriter().write(createLiveStandingsKey(), stats);
    }

    private String createLiveStandingsKey() {
        return String.format(KEY_PATH_FORMAT, GlobalConfig.MatchInfoRoot, _leagueId, _gameweek);
    }
}
