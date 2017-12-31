package data;

import data.eplapi.EntryData;
import data.eplapi.Standing;

import java.util.ArrayList;
import java.util.List;

public class ProcessedMatchTeam extends ProcessedTeam {
    public Standing standing;

    public ProcessedMatchTeam(ProcessedTeam baseTeam, Standing stand) {
        super(baseTeam.id, baseTeam.entry, baseTeam.picks, baseTeam.score, baseTeam.events);
        standing = stand;
    }

    public ProcessedMatchTeam(int teamId, EntryData ent, Standing stand, ArrayList<ProcessedPick> processedPicks, Score s, List<TeamMatchEvent> eventList) {
        super(teamId, ent, processedPicks, s, eventList);
        standing = stand;
    }
}
