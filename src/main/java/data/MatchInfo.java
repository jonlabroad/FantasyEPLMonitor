package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchInfo {
    public int gameweek;
    public Map<Integer, ProcessedTeam> teams = new HashMap<>();

    public MatchInfo(int gw, ProcessedTeam team1, ProcessedTeam team2) {
        gameweek = gw;
        teams.put(team1.id, team1);
        teams.put(team2.id, team2);
    }

    public List<MatchEvent> getAllEventsSorted() {
        List<MatchEvent> allEvents = new ArrayList<>();
        for (ProcessedTeam team : teams.values()) {
            allEvents.addAll(team.events);
        }
        allEvents.sort(new MatchEventSortComparator());
        return allEvents;
    }
}
