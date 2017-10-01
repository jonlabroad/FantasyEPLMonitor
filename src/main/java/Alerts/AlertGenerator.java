package Alerts;

import Config.GlobalConfig;
import Data.MatchInfo;
import Data.Team;

import java.util.HashSet;

public class AlertGenerator {
    private static final HashSet<MatchInfoDifferenceType> _alertableTypes = new HashSet<MatchInfoDifferenceType>();
    private int _teamId;

    public AlertGenerator(int teamId) {
        _teamId = teamId;

        // TODO config this by user
        _alertableTypes.add(MatchInfoDifferenceType.OVERALL_SCORE);
        _alertableTypes.add(MatchInfoDifferenceType.GOAL);
        _alertableTypes.add(MatchInfoDifferenceType.ASSIST);
    }

    public void Generate(MatchInfo newInfo, MatchInfo oldInfo) {
        MatchInfoDifference diff = new MatchInfoComparer().Compare(oldInfo, newInfo);
        for (int i = 0; i < diff.types.size(); i++) {
            if (_alertableTypes.contains(diff.types.get(i))) {
                SendAlert(diff.types.get(i), diff.additionalText.get(i), newInfo);
            }
        }
    }

    private void SendAlert(MatchInfoDifferenceType type, String additionalText, MatchInfo info) {
        String alertText = CreateAlertText(type, additionalText, info);
        SMSAlertSender alertSender = new SMSAlertSender(GlobalConfig.Secrets.GetUserByTeamId(_teamId).alertPhoneNumber);
        alertSender.SendAlert(_teamId, alertText);
    }

    private String CreateAlertText(MatchInfoDifferenceType type, String text, MatchInfo info) {
        String alertText = String.format("%s!\n%s\n\n", DifferenceTypeToReadableString(type), text);
        for (Team team : info.teams) {
            alertText += String.format("%s: %d\n", team.name, team.currentPoints);
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
