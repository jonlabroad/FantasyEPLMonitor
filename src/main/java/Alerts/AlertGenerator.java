package Alerts;

import Config.GlobalConfig;
import Data.MatchInfo;
import Data.ScoreNotification;
import Data.Team;
import org.joda.time.DateTime;

import java.util.HashSet;

import static Alerts.MatchInfoDifferenceType.OVERALL_SCORE;

public class AlertGenerator {
    private static final HashSet<MatchInfoDifferenceType> _alertableTypes = new HashSet<MatchInfoDifferenceType>();
    private int _teamId;
    private boolean _printOnly = true;

    public AlertGenerator(int teamId, boolean printOnly) {
        _teamId = teamId;
        _printOnly = printOnly;

        // TODO config this by user
        _alertableTypes.add(OVERALL_SCORE);
        _alertableTypes.add(MatchInfoDifferenceType.GOAL);
        _alertableTypes.add(MatchInfoDifferenceType.ASSIST);
    }

    public void Generate(MatchInfo newInfo, MatchInfo oldInfo) {
        MatchInfoDifference diff = new MatchInfoComparer(_teamId).Compare(oldInfo, newInfo);
        if (diff.types.contains(OVERALL_SCORE)) {
            Team[] teamsArray = newInfo.teams.values().toArray(new Team[]{});
            ScoreNotification scoreNotification = new ScoreNotification(DateTime.now(),
                    teamsArray[0].currentPoints.startingScore, teamsArray[0].currentPoints.subScore,
                    teamsArray[1].currentPoints.startingScore, teamsArray[1].currentPoints.startingScore,
                    teamsArray[0].name,
                    teamsArray[1].name);

            String overallScoreText = CreateAlertText(OVERALL_SCORE, diff.additionalText.get(diff.types.indexOf(OVERALL_SCORE)), newInfo);
            scoreNotification.addEvent(overallScoreText);
            for (int i = 0; i < diff.types.size(); i++) {
                if (diff.types.get(i) != OVERALL_SCORE) {
                    scoreNotification.addEvent(diff.additionalText.get(i));
                }
            }
            SendAlert(scoreNotification);

            // TODO move this out of here!
            for (String event : scoreNotification.getTickerEvents()) {
                newInfo.matchEvents.add(event);
            }
        }
        else {
            // anything?
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

    private String CreateAlertText(MatchInfoDifferenceType type, String text, MatchInfo info) {
        String alertText = String.format("%s!: %s", DifferenceTypeToReadableString(type), text);
        for (Team team : info.teams.values()) {
            //alertText += String.format("%s: %d (%d)\n", team.name, team.currentPoints.startingScore, team.currentPoints.subScore);
        }
        return alertText;
    }

    private String DifferenceTypeToReadableString(MatchInfoDifferenceType type) {
        switch (type) {
            case OVERALL_SCORE:
                return "Score change";
            case GOAL:
                return "Goal";
            case ASSIST:
                return "Assist";
            default:
                return "";
        }
    }
}
