package client.Youtube;

public class ApiUrl {
    public static String BASE_URL = "https://www.googleapis.com/youtube/v3";

    public static String SEARCH = "/search";
    public static String SEARCH_FMT = BASE_URL + SEARCH + "?part=snippet&channelId=%s&q=%s&type=playlist&key=%s";

    public static String PLAYLIST_ITEMS = "/playlistItems";
    public static String PLAYLIST_ITEMS_FMT = BASE_URL + PLAYLIST_ITEMS + "?part=snippet&maxResults=50&playlistId=%s&key=%s";
}
