package client.Request;

import java.util.HashMap;
import java.util.Map;

public class RecordCollection {
    public HashMap<String, Record> records = new HashMap<>();

    public void merge(RecordCollection other) {
        for (Map.Entry<String, Record> r : other.records.entrySet()) {
            records.put(r.getKey(), r.getValue());
        }
    }
}
