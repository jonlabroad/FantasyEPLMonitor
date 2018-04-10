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
    public EntryData entry;
    public String activeChip;

    public List<TeamMatchEvent> events;
    public List<TeamMatchEvent> autosubs = new ArrayList<>();

    public ProcessedTeam() {}

    public ProcessedTeam(int teamId, EntryData ent, ArrayList<ProcessedPick> processedPicks, Score s, List<TeamMatchEvent> eventList, String chip) {
        id = teamId;
        picks = processedPicks;
        score = s;
        events = eventList;
        entry = ent;
        activeChip = chip;
        if (!activeChip.isEmpty()) {
            System.out.println(entry.entry.name + ": " + activeChip);
        }
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
