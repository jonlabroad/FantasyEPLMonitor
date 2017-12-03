package processor.alert;

import config.AlertProcessorConfig;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

public class ConfigProvider {
    public AlertProcessorConfig read() {
        S3JsonReader reader = new S3JsonReader();
        return reader.read(createKey(), AlertProcessorConfig.class);
    }

    public void write(AlertProcessorConfig config) {
        S3JsonWriter writer = new S3JsonWriter();
        writer.write(createKey(), config);

    }

    private String createKey() {
        return "alertprocessorconfig.json";
    }
}
