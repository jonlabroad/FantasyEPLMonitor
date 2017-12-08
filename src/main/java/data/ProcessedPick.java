package data;

import data.eplapi.Pick;

public class ProcessedPick {
    public ProcessedPlayer footballer;
    public Pick pick;

    public ProcessedPick(ProcessedPlayer player, Pick p) {
        footballer = player;
        pick = p;
    }

    public boolean equals(ProcessedPick o) {
        // Ignoring Vice Captainship since we don't really know if it will matter in the end
        return footballer.rawData.footballer.id == o.footballer.rawData.footballer.id &&
                pick.is_captain == o.pick.is_captain &&
                pick.multiplier == o.pick.multiplier;
    }

    public boolean isCaptain() {
        return pick.is_captain;
    }

    public boolean isViceCaptain() {
        return pick.is_vice_captain;
    }

    public int getMultiplier() {
        return pick.multiplier;
    }
}
