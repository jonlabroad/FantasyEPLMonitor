package alerts;

import data.MatchInfo;
import data.Team;

public class AlertGenerator {

    int _teamId;
    IAlertSender _sender;

    public AlertGenerator(int teamId, IAlertSender sender) {
        _sender = sender;
        _teamId = teamId;
    }

    public void generateAlerts(MatchInfo newInfo, MatchInfo oldInfo) {
        int numNewEvents = newInfo.matchEvents.size() - (oldInfo != null ? oldInfo.matchEvents.size() : 0);
        if (numNewEvents > 0) {
            if (_sender != null) {
                String title = generateAlertTitle(newInfo);
                String subtitle = generateAlertSubtitle(numNewEvents);
                _sender.sendAlert(_teamId, title, subtitle);
            }
        }
    }

    public String generateAlertTitle(MatchInfo info) {
        Team team1 = info.teams.get(info.teamIds.get(0));
        Team team2 = info.teams.get(info.teamIds.get(1));

        return String.format("%s %d - %d %s", team1.name, team1.currentPoints.startingScore,
                team2.currentPoints.startingScore, team2.name);
    }

    public String generateAlertSubtitle(int numNewEvents) {
        return String.format("%d new events!", numNewEvents);
    }
}
