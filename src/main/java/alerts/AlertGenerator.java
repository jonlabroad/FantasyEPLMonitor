package alerts;

import data.MatchInfo;
import data.ScoreNotification;
import data.Team;
import org.joda.time.DateTime;

import java.util.HashSet;

import static alerts.MatchInfoDifferenceType.OVERALL_SCORE;

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
            int team1Id = newInfo.teamIds.get(0);
            int team2Id = newInfo.teamIds.get(1);
            Team team1 = newInfo.teams.get(team1Id);
            Team team2 = newInfo.teams.get(team2Id);
            ScoreNotification scoreNotification = new ScoreNotification(DateTime.now(),
                    team1.currentPoints.startingScore, team1.currentPoints.subScore,
                    team2.currentPoints.startingScore, team2.currentPoints.subScore,
                    team1.name,
                    team2.name);

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
        for (int teamId : info.teamIds) {
            Team team = info.teams.get(teamId);
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
