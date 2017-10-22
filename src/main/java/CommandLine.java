import Runner.GamedayRunner;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //PregameRunner pregame = new PregameRunner();
        //pregame.run();

        GamedayRunner runner = new GamedayRunner();
        runner.run();
    }
}
