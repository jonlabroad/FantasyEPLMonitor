package runner;

import cache.FootballerDataCache;
import client.EPLClient;
import client.EPLClientFactory;
import client.MatchInfoProvider;
import config.DeviceConfig;
import config.GlobalConfig;
import persistance.S3MatchInfoDatastore;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public abstract class CommonRunner {
    protected EPLClient _client;
    protected MatchInfoProvider _matchInfoProvider;
    protected S3MatchInfoDatastore _matchInfoDatastore;

    protected HashSet<Integer> _teamIds = new HashSet<>();
    protected Integer _leagueId = 31187;
    protected boolean _forceUpdate = false;

    public CommonRunner() {
        if (GlobalConfig.TestMode) {
            _teamIds.add(2365803); //me
            _forceUpdate = false;
        }
        else {
            _teamIds.add(1326527);
            _teamIds.add(2365803);
            //_teamIds.add(3303381);
            for (DeviceConfig config : GlobalConfig.DeviceConfig.values()) {
                _teamIds.addAll(config.getAllTeamIds());
            }
        }
    }

    public CommonRunner(Collection<Integer> teamIds) {
        _teamIds.addAll(teamIds);
    }

    public CommonRunner(HashSet<Integer> teamIds) {
        _teamIds = teamIds;
    }

    public void run() {
        _client = null;
        _client = EPLClientFactory.createClient();
        _matchInfoProvider = new MatchInfoProvider(_leagueId, _client);

        _matchInfoDatastore = new S3MatchInfoDatastore(_leagueId);

        for (int teamId : _teamIds) {
            runImpl(teamId);
        }
    }

    public abstract void runImpl(int teamId);
}
