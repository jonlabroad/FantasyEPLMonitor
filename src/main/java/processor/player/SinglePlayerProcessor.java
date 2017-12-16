package processor.player;

import client.DataFilter;
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
    ProcessedPlayerProvider _playerProvider;

    public SinglePlayerProcessor(ProcessedPlayerProvider playerProvider, int gameweek, Footballer footballer, FootballerDetails currentData) {
        _footballer = footballer;
        _currentDetails = currentData;
        _gameweek = gameweek;

        _playerProvider = playerProvider;
        _previousData = _playerProvider.getPlayer(footballer.id);
    }

    public ProcessedPlayer process() {
        if (_currentDetails == null) {
            System.out.format("No details: %s\n", _footballer.web_name);
            return _previousData;
        }

        ProcessedPlayer currentPlayerData = new ProcessedPlayer(_footballer, _currentDetails, _previousData);
        FootballerScoreDetailElement diff = getPlayerDiff();
        if (_previousData != null) {
            diff = new DataFilter(_currentDetails, _previousData.rawData.details, diff).filter();
        }
        addNewEvents(currentPlayerData.events, diff, _footballer, getScoreExplain(_currentDetails));

        return currentPlayerData;
    }

    private FootballerScoreDetailElement getPlayerDiff() {
        return getScoreExplain(_currentDetails).compare(getScoreExplain(_previousData != null ? _previousData.rawData.details : null));
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
