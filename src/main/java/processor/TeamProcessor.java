package processor;

import processor.team.SingleTeamProcessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TeamProcessor {

    private Collection<Integer> _teams;
    private int _leagueId;

    public TeamProcessor(Collection<Integer> teams, int leagueId) {
        _teams = teams;
        _leagueId = leagueId;
    }

    public void process() {
        Set<Integer> teamsProcessed = new HashSet<>();

        for (int teamId : _teams) {
            if (!teamsProcessed.contains(teamId)) {
                // Get the other team in the matchup
                int otherTeamId = -1;

                SingleTeamProcessor processor = new SingleTeamProcessor(teamId, _leagueId);
                processor.process();

                teamsProcessed.add(teamId);
                teamsProcessed.add(otherTeamId);
            }
        }
    }
}
