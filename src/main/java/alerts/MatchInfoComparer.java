package alerts;

import cache.DataCache;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetail;
import data.eplapi.FootballerScoreDetailElement;
import data.MatchInfo;
import data.Team;

import java.util.Map;

public class MatchInfoComparer {

    private int _myTeamId;

    public MatchInfoComparer(int myTeamId) {
        _myTeamId = myTeamId;
    }

    public MatchInfoDifference Compare(MatchInfo oldInfo, MatchInfo newInfo) {
        MatchInfoDifference diff = new MatchInfoDifference();
        CompareScore(diff, oldInfo, newInfo);
        CompareFootballerScores(diff, oldInfo, newInfo);
        return diff;
    }

    private void CompareScore(MatchInfoDifference diff, MatchInfo oldInfo, MatchInfo newInfo) {
        if (oldInfo == null) {
            AddDifference(diff, MatchInfoDifferenceType.OVERALL_SCORE);
            return;
        }
        for (Team team : oldInfo.teams.values()) {
            if (!oldInfo.teams.get(team.id).currentPoints.Equals(newInfo.teams.get(team.id).currentPoints)) {
                AddDifference(diff, MatchInfoDifferenceType.OVERALL_SCORE);
                return;
            }
        }
    }

    private void CompareFootballerScores(MatchInfoDifference diff, MatchInfo oldInfo, MatchInfo newInfo) {
        for (int teamId : newInfo.teamIds) {
            Team team = newInfo.teams.get(teamId);
            for (Map.Entry<Integer, FootballerDetails> detailsEntry : team.footballerDetails.entrySet()) {
                for (int e = 0; e < detailsEntry.getValue().explain.length; e++ ) {
                    FootballerScoreDetail newDetail = detailsEntry.getValue().explain[e];
                    FootballerScoreDetail[] oldDetailArray = null;
                    Team oldTeam = null;
                    if (oldInfo != null) {
                        oldTeam = oldInfo.teams.get(team.id);
                        if (oldTeam.footballerDetails.containsKey(detailsEntry.getKey())) {
                            oldDetailArray = oldTeam.footballerDetails.get(detailsEntry.getKey()).explain;
                        }
                    }

                    FootballerScoreDetailElement oldElement = oldDetailArray != null && e < oldDetailArray.length ? oldDetailArray[e].explain : null;
                    FootballerScoreDetailElement newElement = newDetail.explain;

                    Footballer footballer = DataCache.footballers.get(detailsEntry.getKey());
                    FootballerScoreDetailElement diffElement = newElement.Compare(oldElement);
                    AddDetailDifferences(diff, diffElement, team.id == _myTeamId, footballer);
                }
            }
        }
    }

    private static void AddDetailDifferences(MatchInfoDifference diff, FootballerScoreDetailElement detailsDiff,
                                             boolean isMyTeam, Footballer footballer) {
        String plusMinus = isMyTeam ? "+" : "-";
        if (detailsDiff.assists.value > 0) {
            diff.AddDifference(MatchInfoDifferenceType.ASSIST, plusMinus + "ASSIST " + footballer.second_name);
        }
        if (detailsDiff.goals_scored.value > 0) {
            diff.AddDifference(MatchInfoDifferenceType.GOAL, plusMinus + "GOAL " + footballer.second_name);
        }
    }

    private static void AddDifference(MatchInfoDifference diff, MatchInfoDifferenceType type) {
        diff.AddDifference(type, "");
    }
}
