package processor.player;

import config.GlobalConfig;
import data.ProcessedPlayer;
import data.ProcessedPlayerCollection;
import javafx.util.Pair;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

import java.util.Collections;

public class ProcessedPlayerProvider {
    static final String FILENAME = "players.json";

    int _gameweek;
    String _basePath;

    private S3JsonWriter _writer = new S3JsonWriter();
    private S3JsonReader _reader = new S3JsonReader();

    ProcessedPlayerCollection _playerCache = null;

    public ProcessedPlayerProvider() {
        _gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek;
        _basePath = getBasePath();
        _writer = new S3JsonWriter();
        _reader = new S3JsonReader();
    }

    public ProcessedPlayer getPlayer(int id) {
        if (_playerCache == null) {
            readAllPlayers();
        }

        return _playerCache.players.get(id);
    }

    public void readAllPlayers() {
        _playerCache = new ProcessedPlayerCollection();
        for (String key : _reader.getKeys(getBasePath())) {
            if (key.endsWith(FILENAME)) {
                ProcessedPlayerCollection players = _reader.read(key, ProcessedPlayerCollection.class);
                _playerCache.merge(players);
            }
        }
    }

    public void writePlayers(ProcessedPlayerCollection players) {
        Pair<Integer, Integer> minMax = getMinMaxIds(players);
        String pathName = getPathName(minMax.getKey(), minMax.getValue());
        _writer.write(pathName, players);
    }

    private Pair<Integer, Integer> getMinMaxIds(ProcessedPlayerCollection players) {
        int min = Collections.min(players.players.keySet());
        int max = Collections.max(players.players.keySet());
        return new Pair<>(min, max);
    }

    private String getPathName(int min, int max) {
        return String.format("%s/%d_%d/%s", _basePath, min, max, FILENAME);
    }

    private String getBasePath() {
        return String.format("%s/%d/bins", GlobalConfig.PlayerDataRoot, _gameweek);
    }
}
