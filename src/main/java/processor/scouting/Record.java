package processor.scouting;

public class Record implements Comparable<Record> {
    public int teamId;
    public String teamName;
    public int wins = 0;
    public int draws = 0;
    public int losses = 0;

    public Integer getPoints() {
        return wins*3 + draws;
    }

    @Override
    public int compareTo(Record o) {
        return o.getPoints().compareTo(getPoints());
    }
}
