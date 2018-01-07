package data;

import data.eplapi.Fixture;
import processor.team.DifferentialFinder;

import java.util.*;

public class MatchInfo {
    public int gameweek;
    public Map<Integer, ProcessedMatchTeam> teams = new HashMap<>();
    public List<TeamMatchEvent> allEvents = new ArrayList<>();
    public HashSet<Integer> differentials = new HashSet<Integer>();
    public HashMap<Integer, Fixture> fixtures = new HashMap<>();

    public MatchInfo() {}

    public MatchInfo(int gw, List<TeamMatchEvent> events, ProcessedMatchTeam team1, ProcessedMatchTeam team2, HashMap<Integer, Fixture> fix) {
        gameweek = gw;
        teams.put(team1.id, team1);
        teams.put(team2.id, team2);
        allEvents = events;
        differentials = new DifferentialFinder(team1, team2).find();
        fixtures = fix;
    }
}
