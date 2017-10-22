package config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SecretConfigurator {
    private static final String S3_BUCKET_NAME = GlobalConfig.S3Bucket;
    private static final String S3_KEY_NAME = "secrets.config";
    private AmazonS3 _s3;

    public SecretConfigurator() {
        _s3 = AmazonS3ClientBuilder.defaultClient();
    }

    public SecretConfig ReadConfig() {
        if (_s3.doesObjectExist(S3_BUCKET_NAME, S3_KEY_NAME)) {
            S3Object obj = _s3.getObject(S3_BUCKET_NAME, S3_KEY_NAME);
            return new Gson().fromJson(ReadObject(obj), SecretConfig.class);
        }
        return new SecretConfig();
    }

    public void WriteConfig(SecretConfig config) {
        _s3.putObject(S3_BUCKET_NAME, S3_KEY_NAME, new Gson().toJson(config));
    }

    public String ReadObject(S3Object obj) {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));

        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }
}
