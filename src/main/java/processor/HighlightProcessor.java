package processor;

import client.Youtube.YoutubeClient;
import data.youtube.Item;
import persistance.S3JsonWriter;
import util.HighlightCache;

public class HighlightProcessor {
    private int _gameweek;
    private HighlightCache _highlightCache;

    public HighlightProcessor(int gameweek) {
        _highlightCache = new HighlightCache(gameweek);
        _gameweek = gameweek;
    }

    public void process() {
        try {
            YoutubeClient client = new YoutubeClient();
            Item[] highlights = client.getHighlights(_gameweek);
            if (highlights != null) {
                if (_highlightCache.hasChanged(highlights)) {
                    System.out.println("New highlights available!");
                    new S3JsonWriter().write(
                            String.format("data/highlights/%d/youtube.json", _gameweek),
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
