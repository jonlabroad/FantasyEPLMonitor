package config;

import java.util.Map;

public class GlobalConfig {
    public static final String EplBaseUrl = "https://fantasy.premierleague.com/drf";
    public static final String FootballersPath = "/bootstrap";
    public static final String EntryPath = "/entry/{ENTRY_ID}";
    public static final String EventPath = EntryPath + "/event/{EVENT_ID}";
    public static final String PicksPath = EventPath + "/picks";
    public static final String LeagueH2hPath = "/leagues-h2h-standings/{LEAGUE_ID}";
    public static final String FootballerDetailsPath = "/element-summary/{FOOTBALLER_ID}";

    public static final String S3Bucket = "fantasyeplmatchtracker";
    public static final String TopicNamePrefix = "matchtrackeralert_";
    public static final String TopicArnFormat = String.format("arn:aws:sns:us-east-1:796987500533:%s", TopicNamePrefix) + "%d";

    public static SecretConfig Secrets = new SecretConfigurator().ReadConfig();

    public static boolean Record = false;

    public static Map<String, DeviceConfig> DeviceConfig = new DeviceConfigurator().readAllConfig();
}
