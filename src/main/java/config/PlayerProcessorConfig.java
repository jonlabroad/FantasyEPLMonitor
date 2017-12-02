package config;

import com.google.gson.Gson;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

public class PlayerProcessorConfig {
    public int recorderSequence = 0;
    public boolean record = true;

    private static final String ConfigKey = "playerprocessorconfig.json";
    private static PlayerProcessorConfig instance = null;

    protected PlayerProcessorConfig() {
    }

    public static synchronized PlayerProcessorConfig getInstance() {
        if(instance == null) {
            try {
                instance = readCloud();
            }
            catch (Exception ex) {
                System.out.format("PlayerProcessorConfig not found at %s\n", ConfigKey);
                instance = new PlayerProcessorConfig();
            }
        }
        return instance;
    }

    private static PlayerProcessorConfig readCloud() {
        S3JsonReader reader = new S3JsonReader();
        System.out.format("Reading %s %s\n", GlobalConfig.S3Bucket, ConfigKey);
        return reader.read(ConfigKey, PlayerProcessorConfig.class);
    }

    public void write() {
        S3JsonWriter writer = new S3JsonWriter();
        System.out.format("Writing config: %s\n", new Gson().toJson(this));
        writer.write(ConfigKey, this);
    }

    public PlayerProcessorConfig refresh() {
        instance = readCloud();
        return instance;
    }
}
