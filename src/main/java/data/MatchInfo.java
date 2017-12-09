package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchInfo {
    public int gameweek;
    public Map<Integer, ProcessedTeam> teams = new HashMap<>();
    public List<TeamMatchEvent> allEvents = new ArrayList<>();

    public MatchInfo(int gw, ProcessedTeam team1, ProcessedTeam team2) {
        gameweek = gw;
        teams.put(team1.id, team1);
        teams.put(team2.id, team2);
    }

    public void mergeEvents() {
        for (ProcessedTeam team : teams.values()) {
            for (MatchEvent event : team.events) {
                ProcessedPick pick = team.getPick(event.footballerId);
                TeamMatchEvent tEvent = new TeamMatchEvent(team.id, pick.isCaptain(), pick.getMultiplier(), event);
                allEvents.add(tEvent);
            }
        }
        allEvents.sort(new MatchEventSortComparator());
    }
}
