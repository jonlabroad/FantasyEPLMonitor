package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dispatcher.PlayerProcessorDispatcher;

import java.util.HashMap;
import java.util.Map;

public class PlayerProcessorDispatcherLambda implements RequestHandler<Map<String, Object>, String> {
    public String handleRequest(Map<String, Object> params, Context context) {
        PlayerProcessorDispatcher dispatcher = new PlayerProcessorDispatcher();
        dispatcher.dispatchAll();

        return null;
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        new PlayerProcessorDispatcherLambda().handleRequest(params, null);
    }
}