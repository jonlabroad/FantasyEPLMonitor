package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dispatcher.GamedayDispatcher;

import java.util.Map;

public class GamedayDispatcherLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        new GamedayDispatcher().dispatch();

        return null;
    }
}
