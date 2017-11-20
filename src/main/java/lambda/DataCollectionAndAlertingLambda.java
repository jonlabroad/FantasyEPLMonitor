package lambda;

import runner.GamedayRunner;
import runner.PregameRunner;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.ArrayList;
import java.util.Map;

public class DataCollectionAndAlertingLambda implements RequestHandler<Map<String, Object>, Void>{
    public Void handleRequest(Map<String, Object> params, Context context) {

        ArrayList<Integer> teamIds = readTeamIds(params);
        GamedayRunner runner = null;
        if (teamIds.size() <= 0) {
            runner = new GamedayRunner();
        }
        else {
            runner = new GamedayRunner(teamIds);
        }
        runner.run();
        return null;
    }

    public ArrayList<Integer> readTeamIds(Map<String, Object> params) {
        try {
            Integer[] teamIds = (Integer[]) params.get("teamIds");
            ArrayList<Integer> teamIdsList = new ArrayList<>();
            for (int teamId : teamIds) {
                teamIdsList.add(teamId);
            }
            return teamIdsList;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
