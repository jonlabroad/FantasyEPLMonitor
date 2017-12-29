package data;

import processor.team.DifferentialFinder;

import java.util.*;

public class MatchInfo {
    public int gameweek;
    public Map<Integer, ProcessedTeam> teams = new HashMap<>();
    public List<TeamMatchEvent> allEvents = new ArrayList<>();
    public HashSet<Integer> differentials = new HashSet<Integer>();

    public MatchInfo() {}

    public MatchInfo(int gw, ProcessedTeam team1, ProcessedTeam team2) {
        gameweek = gw;
        teams.put(team1.id, team1);
        teams.put(team2.id, team2);
        differentials = new DifferentialFinder(team1, team2).find();
    }

    public void mergeEvents() {
        for (ProcessedTeam team : teams.values()) {
            allEvents.addAll(team.events);
        }
        allEvents.sort(new MatchEventSortComparator());
    }
}
