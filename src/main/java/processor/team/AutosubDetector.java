package processor.team;

import data.MatchEvent;
import data.MatchEventType;
import data.ProcessedPick;
import data.TeamMatchEvent;
import org.joda.time.DateTime;
import processor.player.PlayerEventGenerator;

import java.util.*;

public class AutosubDetector {
    Set<Integer> startingPositions = new HashSet<>();
    Set<Integer> subPositions = new HashSet<>();

    public AutosubDetector()  {
        for (int i = 1; i <= 11; i++) {
            startingPositions.add(i);
        }
        for (int i = 12; i <= 15; i++) {
            subPositions.add(i);
        }
    }

    public List<TeamMatchEvent> detectAutoSubs(int teamId, List<ProcessedPick> oldPicks, List<ProcessedPick> newPicks) {
        List<TeamMatchEvent> subEvents = new ArrayList<>();
        if (oldPicks == null || newPicks == null) {
            return subEvents;
        }

        for (ProcessedPick newPick : newPicks) {
            boolean oldSub = isSub(oldPicks.get(newPick.footballer.rawData.footballer.id));
            boolean newSub = isSub(newPick);
            if (oldSub && !newSub) {
                MatchEvent event = PlayerEventGenerator.createMatchEvent(DateTime.now(),
                        MatchEventType.AUTOSUB,
                        newPick.footballer.rawData.footballer,
                        1,
                        0);
                TeamMatchEvent tEvent = new TeamMatchEvent(teamId, newPick.isCaptain(), newPick.getMultiplier(), event);
                subEvents.add(tEvent);
            }
        }
        return subEvents;
    }

    private boolean isSub(ProcessedPick pick) {
        return subPositions.contains(pick.pick.position);
    }
}
