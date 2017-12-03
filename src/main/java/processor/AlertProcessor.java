package processor;

import client.MatchInfoProvider;
import config.AlertProcessorConfig;
import data.MatchEvent;
import data.MatchInfo;
import org.joda.time.DateTime;
import processor.alert.ConfigProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertProcessor {
    AlertProcessorConfig _config;
    int _leagueId;
    MatchInfoProvider _matchInfoProvider;

    Set<Integer> _processedTeams = new HashSet<>();

    public AlertProcessor(int leagueId) {
        AlertProcessorConfig config = readConfig();
        _config = config != null ? config : new AlertProcessorConfig();
        _leagueId = leagueId;
        _matchInfoProvider = new MatchInfoProvider(_leagueId);
    }

    public void process() {
    // Read match info data
        List<MatchInfo> matchInfos = _matchInfoProvider.readAll();

        // Loop through matches and alert if necessary
        for (MatchInfo info : matchInfos) {
            process(info);
        }
    }

    private void process(MatchInfo info) {
        if (alreadyProcessed(info.teams.keySet())) {
            return;
        }

        // Loop through events and find any that have been posted since last processing time
        int numRecentEvents = 0;
        List<MatchEvent> allEvents = info.getAllEventsSorted();
        DateTime lastPollDate = util.Date.fromString(_config.LastProcessTime);
        for (MatchEvent event : allEvents) {
            DateTime eventDate = util.Date.fromString(event.dateTime);
            if (eventDate.compareTo(lastPollDate) > 0) {
                System.out.format("Found new event: %s %s\n", event.footballerName, event.type.toString());
                numRecentEvents++;
            }
        }

        if (numRecentEvents > 0) {
            System.out.format("TODO Send alert %d events found\n", numRecentEvents);
        }

    }

    private boolean alreadyProcessed(Set<Integer> teams) {
        boolean processed = false;
        for (Integer teamId : teams) {
            if (_processedTeams.contains(teamId)) {
                return true;
            }
        }
        return processed;
    }

    private AlertProcessorConfig readConfig() {
        return new ConfigProvider().read();
    }
}
