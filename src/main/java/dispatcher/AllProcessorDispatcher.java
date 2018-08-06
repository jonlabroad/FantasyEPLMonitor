package dispatcher;

import data.PlayerProcessorDispatcherList;
import lambda.AwsLambdaInvoker;
import lambda.ILambdaInvoker;
import lambda.LocalAwsLambdaInvoker;
import util.IParallelizableProcess;

import java.util.HashMap;
import java.util.Map;

public class AllProcessorDispatcher implements IParallelizableProcess {
    public static final String LambdaName = "EPLFantasyDataPolling";
    private ILambdaInvoker _lambdaInvoker;

    public void AllProcessorDispatcher() {
        initialize(false);
    }

    public AllProcessorDispatcher(boolean local) {
        initialize(local);
    }

    @Override
    public void process() {
        dispatch();
    }

    private void dispatch() {
        Map<String, Object> params = generatePayload();
        _lambdaInvoker.invoke(LambdaName, params, false);
    }

    private void initialize(boolean local) {
        _lambdaInvoker = local ? new LocalAwsLambdaInvoker() : new AwsLambdaInvoker();
    }

    private Map<String, Object> generatePayload() {
        return new HashMap<>();
    }
}
