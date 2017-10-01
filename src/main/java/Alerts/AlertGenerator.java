package Alerts;

import Config.GlobalConfig;
import Data.EPLAPI.Footballer;
import Data.MatchInfo;
import Data.Team;

import java.util.HashSet;

import static Alerts.MatchInfoDifferenceType.OVERALL_SCORE;

public class AlertGenerator {
    private static final HashSet<MatchInfoDifferenceType> _alertableTypes = new HashSet<MatchInfoDifferenceType>();
    private int _teamId;

    public AlertGenerator(int teamId) {
        _teamId = teamId;

        // TODO config this by user
        _alertableTypes.add(OVERALL_SCORE);
        _alertableTypes.add(MatchInfoDifferenceType.GOAL);
        _alertableTypes.add(MatchInfoDifferenceType.ASSIST);
    }

    public void Generate(MatchInfo newInfo, MatchInfo oldInfo, Footballer[] footballers) {
        MatchInfoDifference diff = new MatchInfoComparer(_teamId).Compare(oldInfo, newInfo, footballers);
        if (diff.types.contains(OVERALL_SCORE)) {
            String alertText = CreateAlertText(OVERALL_SCORE, diff.additionalText.get(diff.types.indexOf(OVERALL_SCORE)), newInfo);
            for (int i = 0; i < diff.types.size(); i++) {
                if (diff.types.get(i) != OVERALL_SCORE) {
                    alertText += "\n" + diff.additionalText.get(i);
                }
            }
            SendAlert(alertText);
        }
        else {
            // anything?
        }
    }

    private void SendAlert(String alertText) {
        SMSAlertSender alertSender = new SMSAlertSender(_teamId, GlobalConfig.Secrets.GetUserByTeamId(_teamId).alertPhoneNumber);
        alertSender.SendAlert(_teamId, alertText);
    }

    private String CreateAlertText(MatchInfoDifferenceType type, String text, MatchInfo info) {
        String alertText = String.format("%s!\n%s\n", DifferenceTypeToReadableString(type), text);
        for (Team team : info.teams) {
            alertText += String.format("%s: %d (%d)\n", team.name, team.currentPoints.startingScore, team.currentPoints.subScore);
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
