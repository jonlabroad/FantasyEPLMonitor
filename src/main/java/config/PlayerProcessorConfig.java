package config;

import persistance.S3JsonReader;
import persistance.S3JsonWriter;

public class PlayerProcessorConfig {
    public int recorderSequence = 0;
    public boolean record = true;

    private static final String ConfigKey = "playerprocessorconfig.json";
    private static PlayerProcessorConfig instance = null;

    protected PlayerProcessorConfig() {
    }

    public static PlayerProcessorConfig getInstance() {
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
        return reader.read(ConfigKey, PlayerProcessorConfig.class);
    }

    public void write() {
        S3JsonWriter writer = new S3JsonWriter();
        writer.write(ConfigKey, this);
    }
}
