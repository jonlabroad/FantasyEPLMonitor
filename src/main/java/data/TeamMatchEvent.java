package data;

public class TeamMatchEvent extends MatchEvent {
    public int teamId;
    public boolean isCaptain;
    public int multiplier;

    public TeamMatchEvent() {}

    public TeamMatchEvent(int team, boolean isCpt, int mult, MatchEvent event) {
        super(event);
        teamId = team;
        isCaptain = isCpt;
        multiplier = mult;
    }

}
