package Runner;

import Cache.FootballerDataReader;
import Client.EPLClient;
import Client.MatchInfoProvider;
import Persistance.S3MatchInfoDatastore;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;

public abstract class CommonRunner {
    protected EPLClient _client;
    protected MatchInfoProvider _matchInfoProvider;
    protected S3MatchInfoDatastore _matchInfoDatastore;

    protected ArrayList<Integer> _teamIds = new ArrayList<>();
    protected Integer _leagueId = 31187;
    protected boolean _forceUpdate = false;

    public CommonRunner() {
        _teamIds.add(2365803);
        _teamIds.add(1326527);
    }

    public CommonRunner(ArrayList<Integer> teamIds) {
        _teamIds = teamIds;
    }

    public void run() {
        _client = null;
        try {
            _client = new EPLClient();
            _matchInfoProvider = new MatchInfoProvider(_leagueId, _client);
            FootballerDataReader dataReader = new FootballerDataReader(_client);
            dataReader.ReadFootballers();

            _matchInfoDatastore = new S3MatchInfoDatastore();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        for (int teamId : _teamIds) {
            runImpl(teamId);
        }
    }

    public abstract void runImpl(int teamId);
}
