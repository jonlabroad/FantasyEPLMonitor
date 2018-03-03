package processor;

import client.Youtube.YoutubeClient;
import data.youtube.Item;
import persistance.S3JsonWriter;

public class HighlightProcessor {
    private int _gameweek;

    public HighlightProcessor(int gameweek) {
        _gameweek = gameweek;
    }

    public void process() {
        try {
            YoutubeClient client = new YoutubeClient();
            Item[] highlights = client.getHighlights(_gameweek);
            if (highlights != null) {
                new S3JsonWriter().write(
                        String.format("data/highlights/%d/youtube.json", _gameweek),
                        highlights,
                        true);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
