package dispatcher;

import data.PlayerProcessorDispatcherList;
import lambda.AwsLambdaInvoker;
import lambda.ILambdaInvoker;
import lambda.LocalAwsLambdaInvoker;
import util.IParallelizableProcess;

import java.util.Map;

public class SinglePlayerProcessorDispatcher implements IParallelizableProcess {
    public static final String LambdaName = "EPLFantasyPlayerProcessor";

    private int _startFootballer;
    private int _stopFootballer;

    private ILambdaInvoker _lambdaInvoker;

    public void SinglePlayerProcessorDispatcher(int start, int end) {
        initialize(start, end, false);
    }

    public SinglePlayerProcessorDispatcher(int start, int end, boolean local) {
        initialize(start, end, local);
    }

    @Override
    public void process() {
        dispatch();
    }

    private void dispatch() {
        Map<String, Object> params = generatePayload(_startFootballer, _stopFootballer).toLambdaParams();
        _lambdaInvoker.invoke(LambdaName, params, false);
    }

    private void initialize(int start, int stop, boolean local) {
        _startFootballer = start;
        _stopFootballer = stop;
        _lambdaInvoker = local ? new LocalAwsLambdaInvoker() : new AwsLambdaInvoker();
    }

    private PlayerProcessorDispatcherList generatePayload(int start, int end) {
        return new PlayerProcessorDispatcherList(start, end);
    }
}
