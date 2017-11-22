package processor.player;

import config.GlobalConfig;
import data.ProcessedPlayer;
import persistance.S3JsonReader;

public class PlayerReader {
    private S3JsonReader _reader = new S3JsonReader();

    public ProcessedPlayer read(int id) {
        String key = createKey(id);
        return _reader.read(key, ProcessedPlayer.class);
    }

    private String createKey(int id) {
        return String.format("%s/%d/player.json", GlobalConfig.PlayerDataRoot, id);
    }
}
