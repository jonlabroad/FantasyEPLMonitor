package dispatcher;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;
import config.GlobalConfig;
import data.PlayerProcessorDispatcherList;
import persistance.S3JsonReader;

public class SinglePlayerProcessorDispatcher {
    public static final String PATH_FMT = GlobalConfig.RECORDER_PATH_FMT;

    S3JsonReader _reader;
    private int _gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek;

    public static final String LambdaName = "EPLFantasyPlayerProcessor";

    private int _startFootballer;
    private int _stopFootballer;

    public SinglePlayerProcessorDispatcher(int start, int end) {
        _startFootballer = start;
        _stopFootballer = end;
    }

    public void dispatch() {
        invoke(_startFootballer, _stopFootballer);
    }

    private InvokeResult invoke(int start, int end) {
        AWSLambda lambda = AWSLambdaClientBuilder.defaultClient();
        InvokeRequest request = new InvokeRequest();
        request.setInvocationType(InvocationType.RequestResponse);
        request.setFunctionName(LambdaName);
        String payload = generatePayload(start, end);
        request.setPayload(payload);
        request.setSdkRequestTimeout(300000);
        System.out.format("Invoking %s\n", payload);
        InvokeResult result = lambda.invoke(request);
        System.out.format("%d\n", result.getStatusCode());
        return result;
    }

    private String generatePayload(int start, int end) {
        return new Gson().toJson(new PlayerProcessorDispatcherList(start, end));
    }
}
