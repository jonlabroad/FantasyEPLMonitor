package lambda;

import client.EPLClient;
import client.EPLClientFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.eplapi.BootstrapStatic;
import data.eplapi.Event;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import processor.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AllProcessorLambda implements RequestHandler<Map<String, Object>, String> {

    protected EPLClient _client = EPLClientFactory.createClient();
    protected int _leagueId = 31187;

    public String handleRequest(Map<String, Object> params, Context context) {
        AllProcessor processor = new AllProcessor(_leagueId);
        processor.process();
        return null;
    }
}
