package Config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DeviceConfigurator {
    private static final String S3_BUCKET_NAME = GlobalConfig.S3Bucket;
    private static final String S3_KEY_NAME = "device.config";
    private AmazonS3 _s3;

    public DeviceConfigurator() {
        _s3 = AmazonS3ClientBuilder.defaultClient();
    }

    public DeviceConfig readConfig() {
        if (_s3.doesObjectExist(S3_BUCKET_NAME, S3_KEY_NAME)) {
            S3Object obj = _s3.getObject(S3_BUCKET_NAME, S3_KEY_NAME);
            return new Gson().fromJson(readObject(obj), DeviceConfig.class);
        }
        return new DeviceConfig();
    }

    public void writeConfig(DeviceConfig config) {
        _s3.putObject(S3_BUCKET_NAME, S3_KEY_NAME, new Gson().toJson(config));
    }

    public String readObject(S3Object obj) {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }
}
