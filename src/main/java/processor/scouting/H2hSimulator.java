package processor.scouting;

import client.EPLClient;
import client.EPLClientFactory;
import data.eplapi.History;
import data.eplapi.TeamHistory;

import java.util.HashMap;

public class H2hSimulator {
    protected int _team1Id;
    protected int _team2Id;
    protected EPLClient _client;

    public H2hSimulator(EPLClient client, int team1Id, int team2Id) {
        if (client == null) {
            client = EPLClientFactory.createClient();
        }
        _client = client;
        _team1Id = team1Id;
        _team2Id = team2Id;
    }

    public HashMap<Integer, Record> simulate() {
        TeamHistory team1History = getHistory(_team1Id);
        TeamHistory team2History = getHistory(_team2Id);
        if (team1History == null) {
            team1History = team2History;
        }
        else if (team2History == null) {
            team2History = team1History;
        }
        if (team1History == null || team2History == null)
        {
            return new HashMap<>();
        }
        return simulate(team1History, team2History);
    }

    protected HashMap<Integer, Record> simulate(TeamHistory team1, TeamHistory team2) {
        Record r1 = new Record();
        Record r2 = new Record();
        HashMap<Integer, Record> records = new HashMap<>();
        records.put(team1.entry.id, r1);
        records.put(team2.entry.id, r2);
        int minGw = Math.max(findMinGameweek(team1), findMinGameweek(team2));
        int maxGw = Math.min(findMaxGameweek(team1), findMaxGameweek(team2));
        for (int gw = minGw; gw <= maxGw; gw++) {
            updateRecords(gw, team1, team2, r1, r2);
        }
        return records;
    }

    protected void updateRecords(int gameweek, TeamHistory team1, TeamHistory team2, Record r1, Record r2) {
        int pts1 = getGameweekPoints(gameweek, team1);
        int pts2 = getGameweekPoints(gameweek, team2);
        if (pts1 > pts2) {
            r1.wins++;
            r2.losses++;
        }
        else if (pts1 < pts2) {
            r1.losses++;
            r2.wins++;
        }
        else if (pts1 == pts2) {
            r1.draws++;
            r2.draws++;
        }
    }

    protected int getGameweekPoints(int gameweek, TeamHistory team) {
        for (History hist : team.history) {
            if (hist.event == gameweek) {
                return hist.points;
            }
        }
        return 0;
    }

    protected int findMinGameweek(TeamHistory team) {
        int min = 10000;
        for (History hist : team.history) {
            if (hist.event < min) {
                min = hist.event;
            }
        }
        return min;
    }

    protected int findMaxGameweek(TeamHistory team) {
        int max = 0;
        for (History hist : team.history) {
            if (hist.event > max) {
                max = hist.event;
            }
        }
        return max;
    }

    protected TeamHistory getHistory(int teamId) {
        return _client.getHistory(teamId);
    }
}
