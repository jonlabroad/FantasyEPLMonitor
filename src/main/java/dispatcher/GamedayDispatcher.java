package dispatcher;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;
import data.DispatcherTeamList;

import java.util.*;

public class GamedayDispatcher {

    private Collection<Integer> _teamIds = new HashSet<>();

    public GamedayDispatcher() {
    }

    public GamedayDispatcher(Collection teamIds) {
        _teamIds = teamIds;
    }

    public void dispatch() {

        // TODO read a configuration, and also subscribe to S3 events for when the configuration changes
        List<DispatcherTeamList> teamLists = generateDefaultLists();

        AWSLambda lambda = AWSLambdaClientBuilder.defaultClient();

        for (DispatcherTeamList list : teamLists) {
            System.out.format("Dispatching data pollers for teams: %s\n", new Gson().toJson(list.teamIds));
            InvokeRequest request = new InvokeRequest();
            request.setFunctionName("EPLFantasyDataPolling");
            request.setPayload(generatePayload(list));
            InvokeResult result = lambda.invoke(request);
            System.out.println(result.getStatusCode());
        }
    }

    private List<DispatcherTeamList> generateDefaultLists() {
        List<DispatcherTeamList> lists = new ArrayList<>();

        DispatcherTeamList teamList = new DispatcherTeamList();
        teamList.teamIds.add(1326527);
        lists.add(teamList);

        teamList = new DispatcherTeamList();
        teamList.teamIds.add(2365803);
        lists.add(teamList);

        return lists;
    }

    private String generatePayload(DispatcherTeamList teamList) {
        return new Gson().toJson(teamList);
    }
}
