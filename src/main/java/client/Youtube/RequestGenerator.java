package client.Youtube;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import config.GlobalConfig;

public class RequestGenerator {
    public static HttpRequest search(int gameweek) {
        return build(String.format(ApiUrl.SEARCH_FMT,
                GlobalConfig.YoutubeChannelId,
                createPlaylistTitle(gameweek),
                GlobalConfig.Secrets.googleApiKey));
    }

    public static HttpRequest playlistItems(String playlistId) {
        return build(String.format(ApiUrl.PLAYLIST_ITEMS_FMT,
                playlistId, GlobalConfig.Secrets.googleApiKey));
    }

    private static String createPlaylistTitle(int gameweek) {
        return String.format("2017+2018+Premier+League+Season+Matchday+%d", gameweek);
    }

    private static HttpRequest build(String path) {
        return Unirest.get(path);
    }
}
