package alerts;

import cache.DataCache;
import data.*;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetail;
import data.eplapi.FootballerScoreDetailElement;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchInfoComparer {

    public MatchInfoComparer() {
    }

    public List<MatchEvent> Compare(MatchInfo oldInfo, MatchInfo newInfo) {
        Map<Integer, List<MatchEvent>> diff = new HashMap<>();
        for (int teamId : newInfo.teamIds) {
            diff.put(teamId, new ArrayList<>());
        }
        CompareFootballerScores(diff, oldInfo, newInfo);
        List<MatchEvent> events = new ArrayList<>();
        for (int teamId : newInfo.teamIds) {
            if (diff.get(teamId).size() == 0) {
                AddScoreDifferences(diff, oldInfo, newInfo);
            }
            events.addAll(diff.get(teamId));
        }

        printMatchEvents(diff);

        return events;
    }

    private void CompareFootballerScores(Map<Integer, List<MatchEvent>> diff, MatchInfo oldInfo, MatchInfo newInfo) {
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

    private static void AddScoreDifferences(Map<Integer, List<MatchEvent>> diff, MatchInfo prevInfo, MatchInfo newInfo) {
        DateTime time = DateTime.now();
        for (int teamId : newInfo.teamIds) {
            Score prevScore = prevInfo.teams.get(teamId).currentPoints;
            Score newScore = newInfo.teams.get(teamId).currentPoints;
            if (prevInfo == null ||
                    (prevScore.startingScore != newScore.startingScore) ||
                    (prevScore.subScore != newScore.subScore) )
            {
                int diffScore = (newScore.startingScore + newScore.subScore) - (prevScore.startingScore + prevScore.subScore);
                if (diffScore < 0) {
                    System.out.println("EH?");
                }
                MatchEvent event = createMatchEvent(time, MatchEventType.OTHER, null, 0, diffScore, teamId);
                diff.get(teamId).add(event);
            }
        }
    }

    private static void AddDetailDifferences(Map<Integer, List<MatchEvent>> diff, FootballerScoreDetailElement detailsDiff,
                                             int teamId, Footballer footballer) {
        DateTime time = DateTime.now();
        if (detailsDiff.assists.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.ASSIST, footballer, detailsDiff.assists.value, detailsDiff.assists.points, teamId));
        }
        if (detailsDiff.goals_scored.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.GOAL, footballer, detailsDiff.goals_scored.value, detailsDiff.goals_scored.points, teamId));
        }
        if (detailsDiff.minutes.points > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.MINUTES_PLAYED, footballer, detailsDiff.minutes.value, detailsDiff.minutes.points, teamId));
        }
        if (detailsDiff.clean_sheets.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.CLEAN_SHEET, footballer, detailsDiff.clean_sheets.value, detailsDiff.clean_sheets.points, teamId));
        }
        if (detailsDiff.bonus.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.BONUS, footballer, detailsDiff.bonus.value, detailsDiff.bonus.points, teamId));
        }
        if (detailsDiff.yellow_cards.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.YELLOW_CARD, footballer, detailsDiff.yellow_cards.value, detailsDiff.yellow_cards.points, teamId));
        }
        if (detailsDiff.red_cards.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.RED_CARD, footballer, detailsDiff.red_cards.value, detailsDiff.red_cards.points, teamId));
        }
        if (detailsDiff.penalty_misses.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.PENALTY_MISS, footballer, detailsDiff.penalty_misses.value, detailsDiff.penalty_misses.points, teamId));
        }
        if (detailsDiff.goals_conceded.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.GOALS_CONCEDED, footballer, detailsDiff.goals_conceded.value, detailsDiff.goals_conceded.points, teamId));
        }
        if (detailsDiff.saves.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.SAVES, footballer, detailsDiff.saves.value, detailsDiff.saves.points, teamId));
        }
        if (detailsDiff.penalty_saves.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.PENALTY_SAVES, footballer, detailsDiff.penalty_saves.value, detailsDiff.penalty_saves.points, teamId));
        }
        if (detailsDiff.own_goals.value > 0) {
            diff.get(teamId).add(createMatchEvent(time, MatchEventType.OWN_GOALS, footballer, detailsDiff.own_goals.value, detailsDiff.own_goals.points, teamId));
        }
    }

    private void printMatchEvents(Map<Integer, List<MatchEvent>> events) {
        for (List<MatchEvent> teamEvents : events.values()) {
            for (MatchEvent event : teamEvents) {
                System.out.println(String.format("%d %s %s %d", event.number, event.typeString, event.footballerName, event.pointDifference));
            }
        }
    }

    private static MatchEvent createMatchEvent(DateTime time, MatchEventType type, Footballer footballer, int number, int scoreDiff, int teamId) {
        MatchEvent event = new MatchEvent();
        event.type = type;
        if (footballer != null) {
            event.footballerName = footballer.web_name;
            event.footballerId = footballer.id;
        }
        else {
            event.footballerName = "";
            event.footballerId = 0;
        }
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
