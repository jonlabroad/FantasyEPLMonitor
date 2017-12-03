package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import processor.AlertProcessor;

import java.util.Map;

public class AlertProcessorLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        int leagueId = 31187;
        new AlertProcessor(leagueId).process();
        return null;
    }
}
