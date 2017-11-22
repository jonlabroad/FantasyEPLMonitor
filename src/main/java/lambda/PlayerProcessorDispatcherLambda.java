package lambda;

import com.amazonaws.services.lambda.model.InvocationType;
import com.google.gson.Gson;
import data.PlayerProcessorDispatcherList;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import config.GlobalConfig;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PlayerProcessorDispatcherLambda implements RequestHandler<Map<String, Object>, Void> {
    public Void handleRequest(Map<String, Object> params, Context context) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        BiFunction<Integer, Integer, InvokeResult> invokeLambda = (start, end) -> invoke(start, end);

        for (int i = 0; i < 600; i += GlobalConfig.NumberFootballersToProcessPerLambda) {
            //Future<InvokeResult> test = invokeLambda.appl(i, i + GlobalConfig.NumberFootballersToProcessPerLambda - 1);
            //executor.submit(invokeLambda);

        }

        return null;
    }

    private InvokeResult invoke(int start, int end) {
        AWSLambda lambda = AWSLambdaClientBuilder.defaultClient();
        InvokeRequest request = new InvokeRequest();
        request.setInvocationType(InvocationType.Event);
        request.setFunctionName("EPLFantasyDataPolling");
        request.setPayload(generatePayload(start, end));
        InvokeResult result = lambda.invoke(request);
        return result;
    }

    private String generatePayload(int start, int end) {
        return new Gson().toJson(new PlayerProcessorDispatcherList(start, end));
    }
}