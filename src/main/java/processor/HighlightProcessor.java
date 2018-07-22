package processor;

import client.Youtube.YoutubeClient;
import config.GlobalConfig;
import data.youtube.Item;
import persistance.S3JsonWriter;
import util.HighlightCache;

public class HighlightProcessor {
    private int _leagueId;
    private int _gameweek;
    private HighlightCache _highlightCache;

    public HighlightProcessor(int gameweek, int leagueId) {
        _highlightCache = new HighlightCache(gameweek);
        _gameweek = gameweek;
        _leagueId = leagueId;
    }

    public void process() {
        try {
            YoutubeClient client = new YoutubeClient();
            Item[] highlights = client.getHighlights(_gameweek);
            if (highlights != null) {
                if (_highlightCache.hasChanged(highlights)) {
                    System.out.println("New highlights available!");
                    new S3JsonWriter().write(
                            String.format(GlobalConfig.DataRoot + "/highlights/%d/youtube.json", _gameweek),
                            highlights,
                            true);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
