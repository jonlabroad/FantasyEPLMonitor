package runner;

import cache.FootballerDataReader;
import client.EPLClient;
import client.MatchInfoProvider;
import config.DeviceConfig;
import config.GlobalConfig;
import persistance.S3MatchInfoDatastore;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.HashSet;

public abstract class CommonRunner {
    protected EPLClient _client;
    protected MatchInfoProvider _matchInfoProvider;
    protected S3MatchInfoDatastore _matchInfoDatastore;

    protected HashSet<Integer> _teamIds = new HashSet<>();
    protected Integer _leagueId = 31187;
    protected boolean _forceUpdate = true;

    public CommonRunner() {
        _teamIds.add(2365803); //me
        /*
        for (DeviceConfig config : GlobalConfig.DeviceConfig.values()) {
            _teamIds.addAll(config.getAllTeamIds());
        }
        */
    }

    public CommonRunner(HashSet<Integer> teamIds) {
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
