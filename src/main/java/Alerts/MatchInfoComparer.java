package Alerts;

import Data.MatchInfo;

public class MatchInfoComparer {

    public static MatchInfoDifference Compare(MatchInfo oldInfo, MatchInfo newInfo) {
        MatchInfoDifference diff = new MatchInfoDifference();
        CompareScore(diff, oldInfo, newInfo);
        return diff;
    }

    private static void CompareScore(MatchInfoDifference diff, MatchInfo oldInfo, MatchInfo newInfo) {
        if (oldInfo == null) {
            AddDifference(diff, MatchInfoDifferenceType.OVERALL_SCORE);
            return;
        }
        for (int i = 0; i < oldInfo.teams.size(); i++) {
            if (oldInfo.teams.get(i).currentPoints != newInfo.teams.get(i).currentPoints) {
                AddDifference(diff, MatchInfoDifferenceType.OVERALL_SCORE);
                return;
            }
        }
    }

    private static void AddDifference(MatchInfoDifference diff, MatchInfoDifferenceType type) {
        diff.AddDifference(type, "");
    }
}
