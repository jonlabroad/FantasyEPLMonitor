package lambda;

import runner.GamedayRunner;
import runner.PregameRunner;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.Map;

public class DataCollectionAndAlerting implements RequestHandler<Map<String, Object>, Void>{
    public Void handleRequest(Map<String, Object> params, Context context) {
        //PregameRunner pregame = new PregameRunner();
        //pregame.run();

        GamedayRunner gameday = new GamedayRunner();
        gameday.run();
        return null;
    }
}
