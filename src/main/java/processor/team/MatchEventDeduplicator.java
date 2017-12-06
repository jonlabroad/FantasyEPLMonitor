package processor.team;

import data.MatchEvent;
import data.ProcessedPick;
import data.ProcessedTeam;

import java.util.List;

public class MatchEventDeduplicator {
    public void deduplicate(ProcessedTeam team1, ProcessedTeam team2) {
        for (MatchEvent team1Event : team1.events) {
            MatchEvent team2Event = getEqualEvent(team1Event, team2.events);
            if (team2Event != null) {
                ProcessedPick team1Pick = team1.getPick(team1Event.footballerId);
                ProcessedPick team2Pick = team2.getPick(team2Event.footballerId);
                if (team1Pick.equals(team2Pick)) {
                    team2.events.remove(team2Event);
                }
            }
        }
    }

    private MatchEvent getEqualEvent(MatchEvent event, List<MatchEvent> events) {
        for (MatchEvent e : events) {
            if (e.equals(event)) {
                return e;
            }
        }
        return null;
    }
}
