package processor.player;

import config.GlobalConfig;
import data.ProcessedPlayer;
import persistance.S3JsonWriter;

public class PlayerWriter {
    private S3JsonWriter _writer = new S3JsonWriter();

    public void write(ProcessedPlayer player) {
        String key = createKey(player);
        _writer.write(key, player);
    }

    private String createKey(ProcessedPlayer player) {
        return String.format("%s/%d/player.json", GlobalConfig.PlayerDataRoot, player.rawData.footballer.id);
    }
}
