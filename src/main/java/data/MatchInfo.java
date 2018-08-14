package data;

import data.eplapi.Fixture;
import processor.scouting.Record;
import processor.team.DifferentialFinder;

import java.util.*;

public class MatchInfo {
    public int gameweek;
    public Map<Integer, ProcessedMatchTeam> teams = new HashMap<>();
    public List<TeamMatchEvent> allEvents = new ArrayList<>();
    public HashSet<Integer> differentials = new HashSet<Integer>();
    public HashMap<Integer, Fixture> fixtures = new HashMap<>();
    public HashMap<Integer, Record> simulatedH2h = new HashMap<>();

    // The only reason this is here is because I'm lazy. It should instead be part of a league-wide structure
    public LiveStandings liveStandings = null;

    public MatchInfo() {}

    public MatchInfo(int gw, List<TeamMatchEvent> events, ProcessedMatchTeam team1, ProcessedMatchTeam team2, HashMap<Integer, Fixture> fix, Record t1H2h, Record t2H2h) {
        gameweek = gw;
        teams.put(team1.id, team1);
        teams.put(team2.id, team2);
        allEvents = events;
        differentials = new DifferentialFinder(team1, team2).find();
        fixtures = fix;
        simulatedH2h = new HashMap<Integer, Record>();
        simulatedH2h.put(team1.id, t1H2h);
        simulatedH2h.put(team2.id, t2H2h);
    }
}
