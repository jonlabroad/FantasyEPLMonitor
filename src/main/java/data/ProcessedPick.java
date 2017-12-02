package data;

import data.eplapi.Pick;

public class ProcessedPick {
    public ProcessedPlayer footballer;
    public Pick pick;

    public ProcessedPick(ProcessedPlayer player, Pick p) {
        footballer = player;
        pick = p;
    }
}
