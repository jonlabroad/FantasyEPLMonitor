package data;

import java.util.HashMap;
import java.util.Map;

public class PlayerProcessorDispatcherList {
    int start;
    int end;

    public PlayerProcessorDispatcherList(int startFootballer, int endFootballer) {
        start = startFootballer;
        end = endFootballer;
    }

    public Map<String, Object> toLambdaParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        return params;
    }
}
