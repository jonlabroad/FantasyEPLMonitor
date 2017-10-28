package config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DeviceConfigurator {
    private static final String S3_BUCKET_NAME = GlobalConfig.S3Bucket;
    private static final String S3_CONFIG_DIR = "device_config";
    private static final String S3_PATH_FMT = S3_CONFIG_DIR + "/%s";
    private static final String S3_KEY_NAME = "device.config";
    private AmazonS3 _s3;

    public DeviceConfigurator() {
        _s3 = AmazonS3ClientBuilder.defaultClient();
    }

    public DeviceConfig readConfig(String uniqueDeviceId) {
        String s3Key = getDeviceConfigPath(uniqueDeviceId);
        if (_s3.doesObjectExist(S3_BUCKET_NAME, s3Key)) {
            return readConfigByKey(s3Key);
        }
        return new DeviceConfig(uniqueDeviceId);
    }

    public DeviceConfig readConfigByKey(String key) {
        S3Object obj = _s3.getObject(S3_BUCKET_NAME, key);
        return new Gson().fromJson(readObject(obj), DeviceConfig.class);
    }

    public Map<String, DeviceConfig> readAllConfig() {
        Map<String, DeviceConfig> configs = new HashMap<>();
        ObjectListing result = _s3.listObjects(S3_BUCKET_NAME, S3_CONFIG_DIR);
        for (S3ObjectSummary summary : result.getObjectSummaries()) {
            DeviceConfig config = readConfigByKey(summary.getKey());
            configs.put(config.uniqueDeviceId, config);
        }
        return configs;
    }

    public void writeConfig(DeviceConfig config, String deviceId) {
        _s3.putObject(S3_BUCKET_NAME, getDeviceConfigPath(deviceId), new Gson().toJson(config));
    }

    public String readObject(S3Object obj) {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }

    private String getDeviceConfigPath(String deviceId) {
        return String.format(S3_PATH_FMT + "/" + S3_KEY_NAME, deviceId);
    }
}
