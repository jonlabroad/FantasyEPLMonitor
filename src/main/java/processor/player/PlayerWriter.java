package processor.player;

import config.GlobalConfig;
import data.ProcessedPlayer;
import persistance.S3JsonWriter;

public class PlayerWriter {
    private S3JsonWriter _writer = new S3JsonWriter();

    public void write(int gameweek, ProcessedPlayer player) {
        String key = createKey(gameweek, player);
        _writer.write(key, player);
    }

    private String createKey(int gameweek, ProcessedPlayer player) {
        return String.format("%s/%d/%d/player.json", GlobalConfig.PlayerDataRoot, gameweek, player.rawData.footballer.id);
    }
}
