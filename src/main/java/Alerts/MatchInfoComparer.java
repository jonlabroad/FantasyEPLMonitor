package Alerts;

import Data.EPLAPI.Footballer;
import Data.EPLAPI.FootballerDetails;
import Data.EPLAPI.FootballerScoreDetail;
import Data.EPLAPI.FootballerScoreDetailElement;
import Data.MatchInfo;
import Data.Team;

import java.util.Map;

public class MatchInfoComparer {

    private int _myTeamId;

    public MatchInfoComparer(int myTeamId) {
        _myTeamId = myTeamId;
    }

    public MatchInfoDifference Compare(MatchInfo oldInfo, MatchInfo newInfo, Footballer[] footballers) {
        MatchInfoDifference diff = new MatchInfoDifference();
        CompareScore(diff, oldInfo, newInfo);
        CompareFootballerScores(diff, oldInfo, newInfo, footballers);
        return diff;
    }

    private void CompareScore(MatchInfoDifference diff, MatchInfo oldInfo, MatchInfo newInfo) {
        if (oldInfo == null) {
            AddDifference(diff, MatchInfoDifferenceType.OVERALL_SCORE);
            return;
        }
        for (int i = 0; i < oldInfo.teams.size(); i++) {
            if (!oldInfo.teams.get(i).currentPoints.Equals(newInfo.teams.get(i).currentPoints)) {
                AddDifference(diff, MatchInfoDifferenceType.OVERALL_SCORE);
                return;
            }
        }
    }

    private void CompareFootballerScores(MatchInfoDifference diff, MatchInfo oldInfo, MatchInfo newInfo,
                                                Footballer[] footballers) {
        for (int t = 0; t < newInfo.teams.size(); t++) {
            Team team = newInfo.teams.get(t);
            for (Map.Entry<Integer, FootballerDetails> detailsEntry : team.footballerDetails.entrySet()) {
                for (int e = 0; e < detailsEntry.getValue().explain.length; e++ ) {
                    FootballerScoreDetail newDetail = detailsEntry.getValue().explain[e];
                    FootballerScoreDetail[] oldDetailArray = null;
                    Team oldTeam = null;
                    if (oldInfo != null) {
                        oldTeam = oldInfo.teams.get(t);
                        if (oldTeam.footballerDetails.containsKey(detailsEntry.getKey())) {
                            oldDetailArray = oldTeam.footballerDetails.get(detailsEntry.getKey()).explain;
                        }
                    }

                    FootballerScoreDetailElement oldElement = oldDetailArray != null && e < oldDetailArray.length ? oldDetailArray[e].explain : null;
                    FootballerScoreDetailElement newElement = newDetail.explain;

                    Footballer footballer = FindFootballer(footballers, detailsEntry.getKey());
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

    // TODO move this out to some sort of hashed footballer datastore
    public static Footballer FindFootballer(Footballer[] footballers, int elementId) {
        for (Footballer footballer : footballers) {
            if (footballer.id == elementId) {
                return footballer;
            }
        }
        return null;
    }
}
