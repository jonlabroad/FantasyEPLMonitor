package persistance;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;

public class FileCachingS3JsonReader extends S3JsonReader {

    private static final String FILE_ROOT = "playback";

    @Override
    public <T> T read(String keyName, Class<T> cls) {
        if (cachedFileExists(keyName)) {
            return readFromFile(keyName, cls);
        }

        T data = super.read(keyName, cls);
        writeToFile(keyName, data);
        return data;
    }

    @Override
    public boolean doesObjectExist(String key) {
        return _client.doesObjectExist(_bucketName, key);
    }

    private <T> T readFromFile(String keyName, Class<T> cls) {
        File file = new File(createFilename(keyName));
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(createFilename(keyName)));
            String dataString = FileUtils.readFileToString(file, Charset.defaultCharset());
            reader.close();

            return new Gson().fromJson(dataString, cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> void writeToFile(String keyName, T data) {
        BufferedWriter writer = null;
        try {
            String filename = createFilename(keyName);
            File file = new File(filename.substring(0, filename.lastIndexOf('/')));
            file.mkdirs();
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write(new Gson().toJson(data));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean cachedFileExists(String keyName) {
        File file = new File(createFilename(keyName));
        return file.exists();
    }

    private String createFilename(String keyName) {
        return FILE_ROOT + "/" + keyName;
    }

}
