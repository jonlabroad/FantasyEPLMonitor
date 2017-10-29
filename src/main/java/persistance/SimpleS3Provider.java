package persistance;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import config.GlobalConfig;

public class SimpleS3Provider {
    protected AmazonS3 _client;
    protected String _bucketName;

    public SimpleS3Provider() {
        init(GlobalConfig.S3Bucket);
    }

    public SimpleS3Provider(String bucketName) {
        init(bucketName);
    }

    private void init(String bucketName) {
        _bucketName = bucketName;
        _client = AmazonS3ClientBuilder.defaultClient();
    }
}
