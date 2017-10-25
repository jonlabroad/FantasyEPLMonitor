package com.jlabroad.eplfantasymatchtracker.config;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;

public class GlobalConfig {
    public static String S3Bucket = "fantasyeplmatchtracker";
    public static String PlatformApplicationId = "arn:aws:sns:us-east-1:796987500533:app/GCM/EPL_Fantasy_MatchTracker";

    public static DeviceConfig deviceConfig;
}
