package data;

import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetailElement;

import java.util.ArrayList;
import java.util.List;

public class ProcessedPlayer {
    public FullFootballerData rawData = new FullFootballerData();
    public List<MatchEvent> events = new ArrayList<>();

    public boolean isCurrentlyPlaying = false;
    public boolean isDonePlaying = false;

    public ProcessedPlayer() {}

    public ProcessedPlayer(Footballer footballer, ArrayList<FootballerScoreDetailElement> explains, ProcessedPlayer oldData) {
        rawData.footballer = footballer;
        rawData.explains = explains;
        if (oldData != null) {
            events.addAll(oldData.events);
        }
    }
}
