package data;

import config.GlobalConfig;
import data.eplapi.Standing;
import data.eplapi.Standings;

public class LiveStandingTeam implements Comparable<LiveStandingTeam> {
    String teamName;
    int teamId;
    Standing standing;
    String liveResult;
    Score currentWeekScore = new Score();

    public LiveStandingTeam(ProcessedTeam team, ProcessedTeam otherTeam, Standings standings)
    {
        Standing oldStanding = findStanding(team.id, standings);
        standing = new Standing(oldStanding);

        teamName = team.entry != null ? team.entry.entry.name : "AVERAGE";
        teamId = team.id;
        currentWeekScore.startingScore = team.score.startingScore;
        currentWeekScore.subScore = team.score.subScore;

        if (doIncrementStandings(standing)) {
            liveResult = "D";
            standing.matches_played++;
            standing.points_for += team.score.startingScore;
            if (team.score.startingScore > otherTeam.score.startingScore) {
                liveResult = "W";
                standing.matches_won++;
                standing.points_total += 3;
            } else if (team.score.startingScore < otherTeam.score.startingScore) {
                liveResult = "L";
                standing.matches_lost++;
                standing.points_total += 0;
            } else {
                standing.matches_drawn++;
                standing.points_total += 1;
            }
        }
    }

    private boolean doIncrementStandings(Standing standing)
    {
        return standing.matches_played < GlobalConfig.CloudAppConfig.CurrentGameWeek;
    }

    private Standing findStanding(int teamId, Standings standings)
    {
        for (Standing result : standings.standings.results)
        {
            if (result.entry == teamId) {
                return result;
            }
        }
        return null;
    }

    @Override
    public int compareTo(LiveStandingTeam o) {
        int comp = Integer.compare(o.standing.points_total, this.standing.points_total);
        if (comp == 0) {
            comp = Integer.compare(o.standing.points_for, this.standing.points_for);
        }
        return comp;
    }
}


