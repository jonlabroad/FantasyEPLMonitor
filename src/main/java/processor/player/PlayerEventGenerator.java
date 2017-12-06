package processor.player;

import data.MatchEvent;
import data.MatchEventType;
import data.eplapi.Footballer;
import data.eplapi.FootballerScoreDetailElement;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class PlayerEventGenerator {
    public List<MatchEvent> createNewEvents(FootballerScoreDetailElement detailsDiff, Footballer footballer, FootballerScoreDetailElement currentDetail) {

        List<MatchEvent> events = new ArrayList<>();
        DateTime time = DateTime.now();
        if (detailsDiff.assists.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.ASSIST, footballer, detailsDiff.assists.value, detailsDiff.assists.points));
        }
        if (detailsDiff.goals_scored.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.GOAL, footballer, detailsDiff.goals_scored.value, detailsDiff.goals_scored.points));
        }
        if (detailsDiff.minutes.points != 0) {
            events.add(createMatchEvent(time, MatchEventType.MINUTES_PLAYED, footballer, currentDetail.minutes.value, detailsDiff.minutes.points));
        }
        if (detailsDiff.clean_sheets.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.CLEAN_SHEET, footballer, detailsDiff.clean_sheets.value, detailsDiff.clean_sheets.points));
        }
        if (detailsDiff.bonus.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.BONUS, footballer, detailsDiff.bonus.value, detailsDiff.bonus.points));
        }
        if (detailsDiff.yellow_cards.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.YELLOW_CARD, footballer, detailsDiff.yellow_cards.value, detailsDiff.yellow_cards.points));
        }
        if (detailsDiff.red_cards.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.RED_CARD, footballer, detailsDiff.red_cards.value, detailsDiff.red_cards.points));
        }
        if (detailsDiff.penalty_misses.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.PENALTY_MISS, footballer, detailsDiff.penalty_misses.value, detailsDiff.penalty_misses.points));
        }
        if (detailsDiff.goals_conceded.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.GOALS_CONCEDED, footballer, detailsDiff.goals_conceded.value, detailsDiff.goals_conceded.points));
        }
        if (detailsDiff.saves.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.SAVES, footballer, detailsDiff.saves.value, detailsDiff.saves.points));
        }
        if (detailsDiff.penalty_saves.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.PENALTY_SAVES, footballer, detailsDiff.penalty_saves.value, detailsDiff.penalty_saves.points));
        }
        if (detailsDiff.own_goals.value != 0) {
            events.add(createMatchEvent(time, MatchEventType.OWN_GOALS, footballer, detailsDiff.own_goals.value, detailsDiff.own_goals.points));
        }
        printMatchEvents(events);
        return events;
    }

    private static MatchEvent createMatchEvent(DateTime time, MatchEventType type, Footballer footballer, int number, int scoreDiff) {
        MatchEvent event = new MatchEvent();
        event.type = type;
        if (footballer != null) {
            event.footballerName = footballer.web_name;
            event.footballerId = footballer.id;
        } else {
            event.footballerName = "";
            event.footballerId = 0;
        }
        event.dateTime = timeToString(time);
        event.typeString = type.toString();
        event.pointDifference = scoreDiff;
        event.number = number;
        return event;
    }

    private static String timeToString(DateTime time) {
        return util.Date.toString(time);
    }

    private void printMatchEvents(List<MatchEvent> events) {
        for (MatchEvent event : events) {
            System.out.println(String.format("%d %s %s %d", event.number, event.typeString, event.footballerName, event.pointDifference));
        }
    }
}
