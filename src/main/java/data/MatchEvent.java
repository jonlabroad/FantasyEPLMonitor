package data;

public class MatchEvent {
    public String dateTime;
    public MatchEventType type;
    public String typeString;
    public int footballerId;
    public String footballerName;
    public int pointDifference;
    public int number;
    public int teamId;

    public boolean appliesToBothTeams() {
        return teamId < 0;
    }

    @Override
    public boolean equals(Object otherObj) {
        MatchEvent other = (MatchEvent) otherObj;
        return  type == other.type &&
                footballerId == other.footballerId &&
                pointDifference == other.pointDifference &&
                number == other.number;
    }

    public String typeToReadableString(int number) {
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
            case AUTOSUB:
                return "Autosub";
            default:
                return type.toString();
        }
    }
}
