package Runner;

import Cache.FootballerDataReader;
import Client.EPLClient;
import Data.MatchInfo;
import Persistance.IMatchInfoDatastore;
import Persistance.S3MatchInfoDatastore;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;

public abstract class CommonRunner {
    protected ArrayList<Integer> _teamIds = new ArrayList<>();
    protected Integer _leagueId = 31187;
    protected boolean _forceUpdate = false;

    protected MatchInfo _thisMatchInfo = null;
    protected MatchInfo _prevThisMatchInfo = null;

    protected MatchInfo _nextMatchInfo = null;
    protected MatchInfo _prevNextMatchInfo = null;

    public CommonRunner() {
        _teamIds.add(2365803);
        _teamIds.add(1326527);
    }

    public CommonRunner(ArrayList<Integer> teamIds) {
        _teamIds = teamIds;
    }

    public void Run() {
        EPLClient client = null;
        try {
            client = new EPLClient();
            FootballerDataReader dataReader = new FootballerDataReader(client);
            dataReader.ReadFootballers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        for (int teamId : _teamIds) {
            try {
                _thisMatchInfo = client.GetMatchInfo(_leagueId, teamId, false);
                _nextMatchInfo = client.GetMatchInfo(_leagueId, teamId, true);
                IMatchInfoDatastore datastore = new S3MatchInfoDatastore(teamId);
                if (!_forceUpdate) {
                    _prevThisMatchInfo = datastore.ReadMatchInfo(teamId, _thisMatchInfo.match.event);
                    _prevNextMatchInfo = datastore.ReadNextMatchInfo(teamId, _thisMatchInfo.match.event + 1);

                    // Update this match info with previous events. This should probably be done elsewhere
                    if (_prevThisMatchInfo != null) {
                        ArrayList<String> thisMatchEvents = _thisMatchInfo.matchEvents;
                        _thisMatchInfo.matchEvents = new ArrayList<String>();
                        for (String event : _prevThisMatchInfo.matchEvents) {
                            _thisMatchInfo.matchEvents.add(event);
                        }
                        for (String event : thisMatchEvents) {
                            _thisMatchInfo.matchEvents.add(event);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            RunImpl(teamId);
        }
    }

    public abstract void RunImpl(int teamId);
}
