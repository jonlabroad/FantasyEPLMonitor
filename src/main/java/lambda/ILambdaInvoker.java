package lambda;

import java.util.Map;

public interface ILambdaInvoker {
    void invoke(String lambdaName, Map<String, Object> params, boolean async);
}
