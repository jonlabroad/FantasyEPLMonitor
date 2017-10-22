package data.eplapi;

public class ScoreExplain {
    public int points;
    public String name;
    public int value;

    public ScoreExplain diff(ScoreExplain other) {
        ScoreExplain diff = new ScoreExplain();
        diff.name = other.name;
        diff.points = points - other.points;
        diff.value = value - other.value;
        return diff;
    }
}
