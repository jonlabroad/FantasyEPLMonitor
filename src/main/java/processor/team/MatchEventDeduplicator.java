package processor.team;

import data.MatchEvent;
import data.ProcessedPick;
import data.ProcessedTeam;
import data.TeamMatchEvent;

import java.util.List;

public class MatchEventDeduplicator {
    public void deduplicate(ProcessedTeam team1, ProcessedTeam team2) {
        for (TeamMatchEvent team1Event : team1.events) {
            TeamMatchEvent team2Event = getEqualEvent(team1Event, team2.events);
            if (team2Event != null) {
                ProcessedPick team1Pick = team1.getPick(team1Event.footballerId);
                ProcessedPick team2Pick = team2.getPick(team2Event.footballerId);
                if (team1Pick.equals(team2Pick)) {
                    team1Event.teamId = -1;
                    team2.events.remove(team2Event);
                }
            }
        }
    }

    private TeamMatchEvent getEqualEvent(MatchEvent event, List<TeamMatchEvent> events) {
        for (TeamMatchEvent e : events) {
            MatchEvent baseEvent = e;
            if (baseEvent.equals(event)) {
                return e;
            }
        }
        return null;
    }
}
