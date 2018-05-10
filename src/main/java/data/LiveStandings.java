package data;

import data.eplapi.Standings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LiveStandings {
    public List<LiveStandingTeam> liveStandings = new ArrayList<>();

    public LiveStandings(Collection<MatchInfo> matchInfos, Standings standings)
    {
        for (MatchInfo matchInfo : matchInfos)
        {
            ProcessedTeam[] teams = matchInfo.teams.values().toArray(new ProcessedTeam[]{});
            LiveStandingTeam team1 = new LiveStandingTeam(teams[0], teams[1], standings);
            LiveStandingTeam team2 = new LiveStandingTeam(teams[1], teams[0], standings);
            liveStandings.add(team1);
            liveStandings.add(team2);
        }
    }
}
