package processor.team;

import client.EPLClient;
import config.GlobalConfig;
import data.EventInfo;
import data.eplapi.Club;
import data.eplapi.Live;
import persistance.S3JsonWriter;

import java.util.ArrayList;

public class EventProcessor {

    private EPLClient _client;
    private int _gameweek;

    public EventProcessor(EPLClient client, int gameweek)
    {
        _client = client;
        _gameweek = gameweek;
    }

    public void process()
    {
        writeEventInfo();
    }

    private void writeEventInfo() {
        Live liveData = _client.getLiveData(_gameweek);
        ArrayList<Club> clubs = _client.getClubs();

        EventInfo eventInfo = new EventInfo();
        eventInfo.event = _gameweek;
        eventInfo.fixtures = liveData.fixtures;
        eventInfo.clubs = clubs;
        new S3JsonWriter().write(String.format(GlobalConfig.DataRoot + "/events/%s/EventInfo", _gameweek), eventInfo, true);
    }
}
