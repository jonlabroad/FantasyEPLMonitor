package alerts;

import cache.DataCache;
import data.eplapi.Footballer;
import data.eplapi.Pick;
import data.MatchInfo;
import data.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ScoutingReport {
    private static final int NUM_KEY_PLAYERS = 5;
    private static final int NUM_DIFFERENCE_MAKERS = 5;

    MatchInfo info;
    public HashMap<Integer, ArrayList<Footballer>> keyPlayers = new HashMap<>();
    public HashMap<Integer, ArrayList<Footballer>> differenceMakers = new HashMap<>();

    public ScoutingReport(MatchInfo matchInfo, boolean isPregame) {
        info = matchInfo;
        Analyze(isPregame);
    }

    protected void Analyze(boolean isPregame) {
        // Find players not in common

        // For pregame, ignore captain status
        FindKeyPlayers(isPregame);
    }

    private void FindKeyPlayers(boolean isPregame) {
        for (Team team : info.teams.values()) {
            ArrayList<Pick> sortedPickList = new ArrayList<>();
            for (Pick pick : team.picks.picks) {
                sortedPickList.add(pick);
            }
            Collections.sort(sortedPickList, (o1, o2) -> ComparePicks(o1, o2, !isPregame));
            ArrayList<Footballer> keyPlayersList = new ArrayList<>();
            for (int i = 0; i < NUM_KEY_PLAYERS; i++) {
                Footballer footballer = DataCache.footballers.get(sortedPickList.get(i).element);
                keyPlayersList.add(footballer);
            }
            keyPlayers.put(team.id, keyPlayersList);
        }
    }

    private int ComparePicks(Pick p1, Pick p2, boolean useCaptainship) {
        Footballer footballer1 = DataCache.footballers.get(p1.element);
        Footballer footballer2 = DataCache.footballers.get(p2.element);
        Double score1 = Double.parseDouble(footballer1.points_per_game) * ((useCaptainship && p1.is_captain) ? 2.0 : 1.0);
        Double score2 = Double.parseDouble(footballer2.points_per_game) * ((useCaptainship && p2.is_captain) ? 2.0 : 1.0);
        return score2.compareTo(score1);
    }

    public String toPregameString() {
        String retString = "SCOUTING REPORT\n\n";
        for (Team team : info.teams.values()) {
            retString += String.format("#%d %s (%dW-%dL-%dD)\n", team.standing.rank, team.name,
                    team.standing.matches_won,
                    team.standing.matches_lost,
                    team.standing.matches_drawn);
            retString += String.format("Key players:\n");
            for (Footballer footballer : keyPlayers.get(team.id)) {
                retString += String.format("%s (ppg: %s)\n", footballer.second_name, footballer.points_per_game);
            }
            retString += "\n";
        }
        return retString;
    }
}
