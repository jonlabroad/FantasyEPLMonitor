package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import processor.PlayerProcessor;
import processor.TeamProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AllProcessorLambda implements RequestHandler<Map<String, Object>, String> {
    public String handleRequest(Map<String, Object> params, Context context) {
        ILambdaInvoker invoker = new LocalAwsLambdaInvoker();
        PlayerProcessorConfig.getInstance().refresh(); // There appears to be caching going on (objs not unloaded from mem)
        try {
            invoker.invoke("EPLFantasyPlayerProcessor", new HashMap<>(), false);
            invoker.invoke("EPLFantasyTeamProcessor", new HashMap<>(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
