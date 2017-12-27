package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.PlayerProcessorConfig;
import processor.PlayerProcessor;

import java.io.IOException;
import java.util.Map;

public class PlayerProcessorLambda implements RequestHandler<Map<String, Object>, String> {
    public String handleRequest(Map<String, Object> params, Context context) {
        Integer[] range = readPlayerRange(params);
        PlayerProcessor processor;
        PlayerProcessorConfig.getInstance().refresh(); // There appears to be caching going on (objs not unloaded from mem)
        if (range == null) {
            processor = new PlayerProcessor();
        }
        else {
            processor = new PlayerProcessor(range[0], range[1]);
        }
        try {
            processor.process();
        } catch (IOException | UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer[] readPlayerRange(Map<String, Object> params) {
        if (params.containsKey("start") && params.containsKey("end")) {
            Integer[] range = new Integer[2];
            range[0] = (Integer) params.get("start");
            range[1] = (Integer) params.get("end");
            System.out.format("Found range: [%d, %d]\n", range[0], range[1]);
            return range;
        }
        else {
            System.out.println("No range found in params");
        }
        return null;
    }
}
