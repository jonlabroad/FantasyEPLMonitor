package alerts;

import data.*;

import java.util.List;

public class MatchEventGenerator {
    private int _teamId;
    private boolean _printOnly = true;

    public MatchEventGenerator(int teamId, boolean printOnly) {
        _teamId = teamId;
        _printOnly = printOnly;
    }

    public void Generate(MatchInfo newInfo, MatchInfo oldInfo) {
        List<MatchEvent> diff = new MatchInfoComparer(_teamId).Compare(oldInfo, newInfo);

        // TODO move this out of here!
        for (MatchEvent event : diff) {
            newInfo.matchEvents.add(event);
        }
    }

    public void GenerateScoutingReport(ScoutingReport report) {
        String alertText = report.toPregameString();
        // TODO reenable scouting report alert
        //SendAlert(alertText);
    }

    private void SendAlert(ScoreNotification alertText) {
        if (!_printOnly) {
            AndroidAlertSender alertSender = new AndroidAlertSender();
            alertSender.SendAlert(_teamId, alertText);
        }
        System.out.format("Alert [%d]: %s\n", _teamId, alertText);
    }

    private String CreateAlertText(MatchEventType type, String text, MatchInfo info) {
        /*
        String alertText = String.format("%s!: %s", info.type(type), text);
        for (int teamId : info.teamIds) {
            Team team = info.teams.get(teamId);
            //alertText += String.format("%s: %d (%d)\n", team.name, team.currentPoints.startingScore, team.currentPoints.subScore);
        }
        return alertText;
        */
        return ""; //TODO?
    }

}
