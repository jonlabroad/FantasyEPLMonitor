package processor.player;

import config.GlobalConfig;
import data.ProcessedPlayer;
import data.ProcessedPlayerCollection;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

import java.util.ArrayList;
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
        int[] minMax = getMinMaxIds(players);
        String pathName = getPathName(minMax[0], minMax[1]);
        _writer.write(pathName, players);
    }

    private int[] getMinMaxIds(ProcessedPlayerCollection players) {
        int[] minMax = new int[2];
        minMax[0] = Collections.min(players.players.keySet());
        minMax[1] = Collections.max(players.players.keySet());
        return minMax;
    }

    private String getPathName(int min, int max) {
        return String.format("%s/%d_%d/%s", _basePath, min, max, FILENAME);
    }

    private String getBasePath() {
        return String.format("%s/%d/bins", GlobalConfig.PlayerDataRoot, _gameweek);
    }
}
