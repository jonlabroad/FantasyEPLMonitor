package lambda;

import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class LocalAwsLambdaInvoker implements ILambdaInvoker {

    @Override
    public void invoke(String lambdaName, Map<String, Object> params, boolean async) {
        RequestHandler<Map<String, Object>, Void> lambda = createLambda(lambdaName);
        lambda.handleRequest(params, null);
    }

    private RequestHandler<Map<String, Object>, Void> createLambda(String lambdaName) {
        if (lambdaName.equals("EPLFantasyPlayerProcessor")) {
            return new PlayerProcessorLambda();
        }
        else if (lambdaName.equals("EPLFantasyPlayerProcessorDispatcher")) {
            return new PlayerProcessorDispatcherLambda();
        }

        throw new RuntimeException(String.format("Do not understand lambda of name: %s", lambdaName));
    }
}
