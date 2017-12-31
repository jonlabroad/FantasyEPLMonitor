package processor.team;

import data.ProcessedPick;
import data.ProcessedTeam;

import java.util.ArrayList;
import java.util.HashSet;

public class DifferentialFinder {
    ArrayList<ProcessedTeam> teams = new ArrayList<ProcessedTeam>();

    public DifferentialFinder(ProcessedTeam team1, ProcessedTeam team2) {
        teams.add(team1);
        teams.add(team2);
    }

    public HashSet<Integer> find() {
        HashSet<Integer> diff = new HashSet<>();

        ArrayList<HashSet<Integer>> captain = new ArrayList<>();
        captain.add(new HashSet<>());
        captain.add(new HashSet<>());

        ArrayList<HashSet<Integer>> starters = new ArrayList<>();
        starters.add(new HashSet<>());
        starters.add(new HashSet<>());

        ArrayList<HashSet<Integer>> subs = new ArrayList<>();
        subs.add(new HashSet<>());
        subs.add(new HashSet<>());

        int teamNum = 0;
        for (ProcessedTeam team : teams) {
            if (team == null || team.picks == null) {
                continue;
            }

            for (ProcessedPick pick : team.picks) {
                if (pick.isCaptain()) {
                    captain.get(teamNum).add(pick.footballer.rawData.footballer.id);
                }
                else if (pick.pick.position <= 11) {
                    starters.get(teamNum).add(pick.footballer.rawData.footballer.id);
                }
                else if (pick.pick.position > 11) {
                    subs.get(teamNum).add(pick.footballer.rawData.footballer.id);
                }
            }
            teamNum++;
        }

        diff.addAll(getExclusion(captain.get(0), captain.get(1)));
        diff.addAll(getExclusion(starters.get(0), starters.get(1)));
        diff.addAll(getExclusion(subs.get(0), subs.get(1)));
        return diff;
    }

    protected HashSet<Integer> getExclusion(HashSet<Integer> set1, HashSet<Integer> set2) {
        HashSet<Integer> diff1 = new HashSet<>(set1);
        HashSet<Integer> diff2 = new HashSet<>(set2);
        diff1.removeAll(set2);
        diff2.removeAll(set1);

        HashSet<Integer> exclusion = new HashSet<>();
        exclusion.addAll(diff1);
        exclusion.addAll(diff2);

        return exclusion;
    }
}
