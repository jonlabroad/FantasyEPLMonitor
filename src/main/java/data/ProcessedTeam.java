package data;

import data.eplapi.Standing;

import java.util.ArrayList;
import java.util.List;

public class ProcessedTeam {
    public int id;
    public ArrayList<ProcessedPick> picks;
    public Score score;
    public Standing standing;

    public List<MatchEvent> events;
    public List<MatchEvent> autosubs = new ArrayList<>();

    public ProcessedTeam(int teamId, Standing stand, ArrayList<ProcessedPick> processedPicks, Score s, List<MatchEvent> eventList) {
        id = teamId;
        picks = processedPicks;
        score = s;
        events = eventList;
        standing = stand;
    }

    public ProcessedPick getPick(int id) {
        for (ProcessedPick pick : picks) {
            if (pick.footballer.rawData.footballer.id == id) {
                return pick;
            }
        }
        return null;
    }

    public void setAutosubs(List<MatchEvent> events) {
        autosubs = events;
    }
}
