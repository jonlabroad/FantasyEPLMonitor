package client.Youtube;

import client.Request.RequestExecutor;
import com.mashape.unirest.request.HttpRequest;
import data.youtube.Item;
import data.youtube.PlaylistItems;
import data.youtube.SearchList;

public class YoutubeClient {
    RequestExecutor executor = new RequestExecutor();

    public Item getPlaylist(int gameweek) {
        HttpRequest request = RequestGenerator.search(gameweek);
        SearchList list = executor.Execute(request, SearchList.class);
        return findItem(list);
    }

    public Item[] getHighlights(int gameweek) {
        Item playlist = getPlaylist(gameweek);
        if (playlist != null && playlist.id != null) {
            HttpRequest request = RequestGenerator.playlistItems(playlist.id.getAsJsonObject().get("playlistId").getAsString());
            PlaylistItems playlistItems = executor.Execute(request, PlaylistItems.class);
            return playlistItems.items;
        }
        return null;
    }

    private Item findItem(SearchList list) {
        if (list.items.length > 0) {
            return list.items[0];
        }
        return null;
    }
}
