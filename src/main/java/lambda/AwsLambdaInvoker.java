package lambda;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;

import java.util.Map;

public class AwsLambdaInvoker implements ILambdaInvoker {

    @Override
    public void invoke(String lambdaName, Map<String, Object> params, boolean async) {
        AWSLambda lambda = AWSLambdaClientBuilder.defaultClient();
        InvokeRequest request = new InvokeRequest();
        request.setInvocationType(InvocationType.RequestResponse);
        request.setFunctionName(lambdaName);
        String payload = new Gson().toJson(params);
        request.setPayload(payload);
        request.setSdkRequestTimeout(300000);
        System.out.format("Invoking %s: %s\n", lambdaName, payload);
        InvokeResult result = lambda.invoke(request);
        System.out.format("%d\n", result.getStatusCode());
    }
}
