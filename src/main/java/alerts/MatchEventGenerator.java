package alerts;

import client.EPLClient;
import data.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchEventGenerator {
    private int _teamId;
    private boolean _printOnly = true;
    private EPLClient _client;

    private Set<MatchEventType> _negativeValueAllowed = new HashSet<>();

    public MatchEventGenerator(int teamId, EPLClient client, boolean printOnly) {
        _teamId = teamId;
        _printOnly = printOnly;
        _client = client;
        _negativeValueAllowed.add(MatchEventType.CLEAN_SHEET);
        _negativeValueAllowed.add(MatchEventType.OTHER);
    }

    public void Generate(LegacyMatchInfo newInfo, LegacyMatchInfo oldInfo) {
        List<MatchEvent> diff = new MatchInfoComparer(_client).Compare(oldInfo, newInfo);
        useOldEvents(newInfo, diff);

        // TODO move this out of here!
        for (MatchEvent event : diff) {
            newInfo.matchEvents.add(event);
        }
    }
/*
    public void GenerateScoutingReport(ScoutingReport report) {
        String alertText = report.toPregameString();
        // TODO reenable scouting report alert
        //SendAlert(alertText);
    }
*/
    private void useOldEvents(LegacyMatchInfo newInfo, List<MatchEvent> diff) {
        // Remove all new events that made the diff negative, if applicable
        List<MatchEvent> diffToRemove = new ArrayList<>();
        for (MatchEvent event : diff) {
            if (event.number < 0) {
                System.out.format("%d, %s, %d\n", event.footballerId, event.type, event.number);
                if (!_negativeValueAllowed.contains(event.type)) {
                    findAndRemoveEvent(newInfo, event.footballerId, event.type);
                    diffToRemove.add(event);
                }
            }
        }
        diff.removeAll(diffToRemove);
    }

    private void findAndRemoveEvent(LegacyMatchInfo newInfo, int footballerId, MatchEventType type) {
        MatchEvent toRemove = null;

        // Assumes that the last event that matches will be the current event
        for (MatchEvent event : newInfo.matchEvents) {
            if (event.footballerId == footballerId && event.type == type) {
                toRemove = event;
            }
        }
        if (toRemove != null) {
            System.out.format("Trimming %d %s %d from new events\n", footballerId, type);
            newInfo.matchEvents.remove(toRemove);
        }
    }
}
