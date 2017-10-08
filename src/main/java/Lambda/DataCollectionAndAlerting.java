package Lambda;

import Alerts.AlertGenerator;
import Client.EPLClient;
import Data.EPLAPI.Footballer;
import Data.MatchInfo;
import Data.Team;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import Runner.GamedayRunner;
import Runner.PregameRunner;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DataCollectionAndAlerting implements RequestHandler<Map<String, Object>, Void>{
    public Void handleRequest(Map<String, Object> params, Context context) {
        PregameRunner pregame = new PregameRunner();
        pregame.Run();

        GamedayRunner gameday = new GamedayRunner();
        gameday.Run();
        return null;
    }
}
