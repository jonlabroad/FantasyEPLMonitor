package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dispatcher.PlayerProcessorDispatcher;

import java.util.Map;

public class PlayerProcessorDispatcherLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        PlayerProcessorDispatcher dispatcher = new PlayerProcessorDispatcher();
        dispatcher.dispatchAll();

        return null;
    }
}