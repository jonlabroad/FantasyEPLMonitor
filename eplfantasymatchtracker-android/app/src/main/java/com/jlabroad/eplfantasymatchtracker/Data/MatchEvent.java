package com.jlabroad.eplfantasymatchtracker.data;

import com.jlabroad.eplfantasymatchtracker.data.eplapi.Footballer;

public class MatchEvent {
    public String dateTime;
    public MatchEventType type;
    public int footballerId;
    public Footballer footballer;
    public int pointDifference;
    public int number;
    public int teamId;

    public String typeToReadableString() {
        String plural = number > 1 ? "s" : "";
        String ePlural = number > 1 ? "es" : "";
        switch (type) {
            case GOAL:
                return "Goal" + plural;
            case ASSIST:
                return "Assist" + plural;
            case MINUTES_PLAYED:
                return "Minute" + plural + " played";
            case CLEAN_SHEET:
                return "Clean sheet" + plural;
            case BONUS:
                return "Bonus";
            case YELLOW_CARD:
                return "Yellow card" + plural;
            case RED_CARD:
                return "Red card" + plural;
            case PENALTY_MISS:
                return "Penalty miss" + ePlural;
            case GOALS_CONCEDED:
                return "Goal" + plural + " conceded";
            case SAVES:
                return "Save" + plural;
            case PENALTY_SAVES:
                return "Penalty save" + plural;
            case OWN_GOALS:
                return "Own goal" + plural;
            default:
                return type.toString();
        }
    }
}
