package alerts;

import cache.DataCache;
import data.MatchEvent;
import data.MatchEventType;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetail;
import data.eplapi.FootballerScoreDetailElement;
import data.MatchInfo;
import data.Team;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchInfoComparer {

    private int _myTeamId;

    public MatchInfoComparer(int myTeamId) {
        _myTeamId = myTeamId;
    }

    public List<MatchEvent> Compare(MatchInfo oldInfo, MatchInfo newInfo) {
        List<MatchEvent> diff = new ArrayList<>();
        CompareFootballerScores(diff, oldInfo, newInfo);
        return diff;
    }

    private void CompareFootballerScores(List<MatchEvent> diff, MatchInfo oldInfo, MatchInfo newInfo) {
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
                    AddDetailDifferences(diff, diffElement, team.id, footballer);
                }
            }
        }
    }

    private static void AddDetailDifferences(List<MatchEvent> diff, FootballerScoreDetailElement detailsDiff,
                                             int teamId, Footballer footballer) {
        DateTime time = DateTime.now();
        if (detailsDiff.assists.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.ASSIST, footballer, detailsDiff.assists.value, detailsDiff.assists.points, teamId));
        }
        if (detailsDiff.goals_scored.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.GOAL, footballer, detailsDiff.goals_scored.value, detailsDiff.goals_scored.points, teamId));
        }
        if (detailsDiff.minutes.points != 0) {
            //diff.add(createMatchEvent(time, MatchEventType.MINUTES_PLAYED, footballer, detailsDiff.minutes.value, detailsDiff.minutes.points, teamId));
        }
        if (detailsDiff.clean_sheets.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.CLEAN_SHEET, footballer, detailsDiff.clean_sheets.value, detailsDiff.clean_sheets.points, teamId));
        }
        if (detailsDiff.bonus.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.BONUS, footballer, detailsDiff.bonus.value, detailsDiff.bonus.points, teamId));
        }
        if (detailsDiff.yellow_cards.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.YELLOW_CARD, footballer, detailsDiff.yellow_cards.value, detailsDiff.yellow_cards.points, teamId));
        }
        if (detailsDiff.red_cards.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.RED_CARD, footballer, detailsDiff.red_cards.value, detailsDiff.red_cards.points, teamId));
        }
        if (detailsDiff.penalty_misses.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.PENALTY_MISS, footballer, detailsDiff.penalty_misses.value, detailsDiff.penalty_misses.points, teamId));
        }
        if (detailsDiff.goals_conceded.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.GOALS_CONCEDED, footballer, detailsDiff.goals_conceded.value, detailsDiff.goals_conceded.points, teamId));
        }
        if (detailsDiff.saves.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.SAVES, footballer, detailsDiff.saves.value, detailsDiff.saves.points, teamId));
        }
        if (detailsDiff.penalty_saves.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.PENALTY_SAVES, footballer, detailsDiff.penalty_saves.value, detailsDiff.penalty_saves.points, teamId));
        }
        if (detailsDiff.own_goals.value > 0) {
            diff.add(createMatchEvent(time, MatchEventType.OWN_GOALS, footballer, detailsDiff.own_goals.value, detailsDiff.own_goals.points, teamId));
        }
    }

    private static MatchEvent createMatchEvent(DateTime time, MatchEventType type, Footballer footballer, int number, int scoreDiff, int teamId) {
        MatchEvent event = new MatchEvent();
        event.type = type;
        event.footballer = footballer;
        event.footballerId = footballer.id;
        event.dateTime = timeToString(time);
        event.typeString = type.toString();
        event.pointDifference = scoreDiff;
        event.number = number;
        event.teamId = teamId;
        return event;
    }

    private static String timeToString(DateTime time) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd HH:mm");
        return fmt.print(time);
    }
}
