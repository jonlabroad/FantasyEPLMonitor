import Alerts.AlertGenerator;
import Alerts.MatchInfoComparer;
import Alerts.MatchInfoDifference;
import Alerts.SMSAlertSender;
import Client.EPLClient;
import Config.GlobalConfig;
import Config.SecretConfig;
import Config.SecretConfigurator;
import Config.User;
import Data.EPLAPI.Footballer;
import Data.MatchInfo;
import Data.Team;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import Runner.GamedayRunner;
import Runner.PregameRunner;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        PregameRunner pregame = new PregameRunner();
        pregame.Run();

        GamedayRunner runner = new GamedayRunner();
        runner.Run();
    }
}
