package config;

import persistance.S3JsonReader;
import persistance.S3JsonWriter;

public class CloudAppConfigProvider {

    private static final String FILENAME = "appconfig.json";

    private S3JsonReader _reader;
    private S3JsonWriter _writer;
    private CloudAppConfig _config;

    public CloudAppConfigProvider() {
        _reader = new S3JsonReader();
        _writer = new S3JsonWriter();
    }

    public CloudAppConfig read() {
        return _reader.read(FILENAME, CloudAppConfig.class);
    }

    public void write(CloudAppConfig config) {
        _config = config;
        _writer.write(FILENAME, config);
    }
}
