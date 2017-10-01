package Client;

public class Score {
    public int startingScore =0;
    public int subScore = 0;

    public boolean Equals(Score other) {
        return (startingScore == other.startingScore) && (subScore == other.subScore);
    }
}
