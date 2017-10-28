package com.jlabroad.eplfantasymatchtracker.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.jlabroad.eplfantasymatchtracker.aws.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DeviceConfigurator {
    private static final String S3_BUCKET_NAME = GlobalConfig.S3Bucket;
    private static final String S3_CONFIG_DIR = "device_config";
    private static final String S3_PATH_FMT = S3_CONFIG_DIR + "/%s";
    private static final String S3_KEY_NAME = "device.config";
    private AmazonS3 _s3;

    public DeviceConfigurator() {
        _s3 = new AmazonS3Client(Credentials.instance().creds);
    }

    public DeviceConfig readConfig(String deviceId) throws IOException {
        String s3Key = getDeviceConfigPath(deviceId);
        if (_s3.doesObjectExist(S3_BUCKET_NAME, s3Key)) {
            return readConfigByKey(s3Key);
        }
        return new DeviceConfig(deviceId);
    }

    public DeviceConfig readConfigByKey(String key) throws IOException {
        S3Object obj = _s3.getObject(S3_BUCKET_NAME, key);
        return new Gson().fromJson(readObject(obj), DeviceConfig.class);
    }

    public Map<String, DeviceConfig> readAllConfig() throws IOException {
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

    public String readObject(S3Object obj) throws IOException {
        String out = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;

            out += line;
        }
        return out;
    }

    private String getDeviceConfigPath(String deviceId) {
        return String.format(S3_PATH_FMT + "/" + S3_KEY_NAME, deviceId);
    }
}
