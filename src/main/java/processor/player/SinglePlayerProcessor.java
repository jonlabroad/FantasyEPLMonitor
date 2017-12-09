package processor.player;

import client.DataFilter;
import config.GlobalConfig;
import data.MatchEvent;
import data.ProcessedPlayer;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetailElement;

import java.util.List;

public class SinglePlayerProcessor {
    int _gameweek;
    Footballer _footballer;
    FootballerDetails _currentDetails;
    ProcessedPlayer _previousData;

    public SinglePlayerProcessor(int gameweek, Footballer footballer, FootballerDetails currentData) {
        _footballer = footballer;
        _currentDetails = currentData;
        _gameweek = gameweek;

        PlayerReader reader = new PlayerReader();
        _previousData = reader.read(GlobalConfig.CloudAppConfig.CurrentGameWeek, footballer.id);
    }

    public ProcessedPlayer process() {
        if (_currentDetails == null) {
            System.out.format("No details: %s\n", _footballer.web_name);
            return _previousData;
        }

        ProcessedPlayer currentPlayerData = new ProcessedPlayer(_footballer, _currentDetails, _previousData);
        FootballerScoreDetailElement diff = getPlayerDiff();
        new DataFilter(_currentDetails, _previousData.rawData.details, diff).filter();
        addNewEvents(currentPlayerData.events, diff, _footballer, getScoreExplain(_currentDetails));

        PlayerWriter writer = new PlayerWriter();
        writer.write(_gameweek, currentPlayerData);

        return currentPlayerData;
    }

    private FootballerScoreDetailElement getPlayerDiff() {
        return getScoreExplain(_currentDetails).Compare(getScoreExplain(_previousData != null ? _previousData.rawData.details : null));
    }

    private static void addNewEvents(List<MatchEvent> diff, FootballerScoreDetailElement detailsDiff, Footballer footballer, FootballerScoreDetailElement currentDetail) {
        PlayerEventGenerator generator = new PlayerEventGenerator();
        List<MatchEvent> newEvents = generator.createNewEvents(detailsDiff, footballer, currentDetail);
        diff.addAll(newEvents);
    }

    private FootballerScoreDetailElement getScoreExplain(FootballerDetails details) {
        if (details == null) {
            return null;
        }
        return details.explain[0].explain;
    }
}
