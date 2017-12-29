package data;

import data.eplapi.Entry;
import data.eplapi.EntryData;
import data.eplapi.Standing;

import java.util.ArrayList;
import java.util.List;

public class ProcessedTeam {
    public int id;
    public ArrayList<ProcessedPick> picks;
    public Score score;
    public Standing standing;
    public EntryData entry;

    public List<TeamMatchEvent> events;
    public List<TeamMatchEvent> autosubs = new ArrayList<>();

    public ProcessedTeam() {}

    public ProcessedTeam(int teamId, EntryData ent, Standing stand, ArrayList<ProcessedPick> processedPicks, Score s, List<TeamMatchEvent> eventList) {
        id = teamId;
        picks = processedPicks;
        score = s;
        events = eventList;
        standing = stand;
        entry = ent;
    }

    public ProcessedPick getPick(int id) {
        for (ProcessedPick pick : picks) {
            if (pick.footballer.rawData.footballer.id == id) {
                return pick;
            }
        }
        return null;
    }

    public void setAutosubs(List<TeamMatchEvent> events) {
        autosubs = events;
    }
}
