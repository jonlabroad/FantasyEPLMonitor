package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import processor.PlayerProcessor;

import java.util.Map;

public class PlayerProcessorLamba implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        PlayerProcessor processor = new PlayerProcessor();

        return null;
    }
}
