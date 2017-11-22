package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mashape.unirest.http.exceptions.UnirestException;
import processor.PlayerProcessor;

import java.io.IOException;
import java.util.Map;

public class PlayerProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        Integer[] range = readPlayerRange(params);
        PlayerProcessor processor;
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
            return range;
        }
        return null;
    }
}
