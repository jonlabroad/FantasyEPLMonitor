package config;

import java.util.Map;

public class GlobalConfig {
    public static final String EplBaseUrl = "https://fantasy.premierleague.com/drf";
    public static final String FootballersPath = "/bootstrap";
    public static final String BootstrapStaticPath = "/bootstrap-static";
    public static final String EntryPath = "/entry/{ENTRY_ID}";
    public static final String EventPath = EntryPath + "/event/{EVENT_ID}";
    public static final String PicksPath = EventPath + "/picks";
    public static final String LeagueH2hPath = "/leagues-h2h-standings/{LEAGUE_ID}";
    public static final String FootballerDetailsPath = "/element-summary/{FOOTBALLER_ID}";
    public static final String LivePath = "/event/{EVENT_ID}/live";
    public static final String LeagueH2hMatchesPath = "/leagues-entries-and-h2h-matches/league/{LEAGUE_ID}?page={PAGE}";
    public static final String HistoryPath = EntryPath + "/history";

    // LegacyMatchInfo
    public static String MatchInfoRoot = "data";

    public static final String S3Bucket = "fantasyeplmatchtracker";
    public static final String PlayerDataRoot = "data/players";
    public static final String TopicNamePrefix = "matchtrackeralert_";
    public static final String TopicArnFormat = String.format("arn:aws:sns:us-east-1:796987500533:%s", TopicNamePrefix) + "%d";

    public static SecretConfig Secrets = new SecretConfigurator().ReadConfig();
    public static CloudAppConfig CloudAppConfig = new CloudAppConfigProvider().read();

    public static final String RECORDER_BASE_PATH = "recorder";
    public static final String RECORDER_PATH_FMT = RECORDER_BASE_PATH + "/%d/%d/%s";

    public static final int NumberFootballersToProcessPerLambda = 600;
    public static final boolean BinPlayerData = false;

    public static boolean LocalLambdas = false;
    public static boolean TestMode = false;
    public static boolean PlaybackMode = false;
    public static int PlaybackGameweek = 10;
    public static int CurrentPlaybackSequence = 0;
    public static boolean Record = false;

    public static Map<String, DeviceConfig> DeviceConfig = new DeviceConfigurator().readAllConfig();
}
