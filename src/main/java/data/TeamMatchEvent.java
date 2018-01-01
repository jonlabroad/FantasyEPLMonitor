package data;

public class TeamMatchEvent extends MatchEvent {
    public int teamId;
    public boolean isCaptain;
    public int multiplier;

    public TeamMatchEvent() {}

    public TeamMatchEvent(TeamMatchEvent other) {
        super(other);
        teamId = other.teamId;
        isCaptain = other.isCaptain;
        multiplier = other.multiplier;
    }

    public TeamMatchEvent(int team, boolean isCpt, int mult, MatchEvent event) {
        super(event);
        teamId = team;
        isCaptain = isCpt;
        multiplier = mult;
    }

    @Override
    public boolean equals(Object otherObj) {
        TeamMatchEvent other = (TeamMatchEvent) otherObj;
        return super.equals(other) &&
                teamId == other.teamId &&
                isCaptain == other.isCaptain &&
                multiplier == other.multiplier;
    }

}
