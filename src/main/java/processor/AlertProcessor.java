package processor;

import alerts.AndroidAlertSender;
import client.MatchInfoProvider;
import config.AlertProcessorConfig;
import data.*;
import org.joda.time.DateTime;
import processor.alert.ConfigProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertProcessor {
    AlertProcessorConfig _config;
    int _leagueId;
    Set<Integer> _teamIds;
    MatchInfoProvider _matchInfoProvider;

    Set<Integer> _processedTeams = new HashSet<>();

    public AlertProcessor(int leagueId, Set<Integer> teamIds) {
        AlertProcessorConfig config = readConfig();
        _config = config != null ? config : new AlertProcessorConfig();
        _leagueId = leagueId;
        _matchInfoProvider = new MatchInfoProvider(_leagueId);
        _teamIds = teamIds != null ? teamIds : new HashSet<>();
    }

    public void process() {
    // Read match info data
        List<MatchInfo> matchInfos = _matchInfoProvider.readAll();

        // Loop through matches and alert if necessary
        for (MatchInfo info : matchInfos) {
            process(info);
        }

        _config.LastProcessTime = util.Date.toString(DateTime.now());
        writeConfig();
    }

    private void process(MatchInfo info) {
        if (alreadyProcessed(info.teams.keySet())) {
            System.out.format("Teams already processed. Skipping alerts\n");
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
            String alertTitle = generateAlertTitle(info);
            String alertText = generateAlertSubtitle(numRecentEvents);
            for (int teamId : info.teams.keySet()) {
                if (shouldAlertTeam(teamId)) {
                    AndroidAlertSender sender = new AndroidAlertSender();
                    sender.sendAlert(teamId, alertTitle, alertText);
                }
            }
        }

        _processedTeams.addAll(info.teams.keySet());
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

    private void writeConfig() {
        new ConfigProvider().write(_config);
    }

    private String generateAlertTitle(MatchInfo info) {
        ArrayList<Integer> teamIds = new ArrayList<>();
        teamIds.addAll(info.teams.keySet());

        ProcessedTeam team1 = info.teams.get(teamIds.get(0));
        ProcessedTeam team2 = info.teams.get(teamIds.get(1));

        return String.format("%s %d - %d %s", team1.standing.entry_name, team1.score.startingScore,
                team2.score.startingScore, team2.standing.entry_name);
    }

    private String generateAlertSubtitle(int numNewEvents) {
        return String.format("%d new events!", numNewEvents);
    }

    private boolean shouldAlertTeam(int teamId) {
        if (_teamIds == null || _teamIds.isEmpty()) {
            return true;
        }
        return _teamIds.contains(teamId);
    }
}
