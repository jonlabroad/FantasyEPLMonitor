package processor.team;

import data.MatchEvent;
import data.ProcessedPick;
import data.ProcessedTeam;
import data.TeamMatchEvent;

import java.util.ArrayList;
import java.util.List;

public class MatchEventDeduplicator {
    public List<TeamMatchEvent> deduplicate(ProcessedTeam team1, ProcessedTeam team2) {
        if (team1.id == team2.id) {
            return team1.events;
        }

        List<TeamMatchEvent> copyEvents = copyEvents(team1.events, team2.events);

        for (TeamMatchEvent team1Event : team1.events) {
            TeamMatchEvent team2Event = getEqualEvent(team1Event, team2.events);
            if (team2Event != null) {
                ProcessedPick team1Pick = team1.getPick(team1Event.footballerId);
                ProcessedPick team2Pick = team2.getPick(team2Event.footballerId);
                if (team1Pick.equals(team2Pick)) {
                    System.out.format("Found identical event: %s %s\n", team1Event.footballerName, team1Event.type.toString());
                    TeamMatchEvent sharedEvent = copyEvents.get(copyEvents.indexOf(team1Event));
                    sharedEvent.teamId = -1;
                    copyEvents.remove(team2Event);
                }
            }
        }
        return copyEvents;
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

    private List<TeamMatchEvent> copyEvents(List<TeamMatchEvent> team1Events, List<TeamMatchEvent> team2Events) {
        ArrayList<TeamMatchEvent> newEvents = new ArrayList<>();
        for (TeamMatchEvent event : team1Events) {
            newEvents.add(new TeamMatchEvent(event));
        }
        for (TeamMatchEvent event : team2Events) {
            newEvents.add(new TeamMatchEvent(event));
        }
        return newEvents;
    }
}
