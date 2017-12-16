package data;

import java.util.HashMap;

public class ProcessedPlayerCollection {
    public HashMap<Integer, ProcessedPlayer> players = new HashMap<>();

    public void merge(ProcessedPlayerCollection other) {
        players.putAll(other.players);
    }
}
