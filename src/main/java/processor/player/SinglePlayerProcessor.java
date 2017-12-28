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
    FootballerScoreDetailElement _currentExplains;
    ProcessedPlayer _previousData;
    ProcessedPlayerProvider _playerProvider;

    public SinglePlayerProcessor(ProcessedPlayerProvider playerProvider, int gameweek, Footballer footballer, FootballerScoreDetailElement explains) {
        _footballer = footballer;
        _currentExplains = explains;
        _gameweek = gameweek;

        _playerProvider = playerProvider;
        _previousData = _playerProvider.getPlayer(footballer.id);
    }

    public ProcessedPlayer process() {
        if (_currentExplains == null) {
            System.out.format("No details: %s\n", _footballer.web_name);
            return _previousData;
        }

        ProcessedPlayer currentPlayerData = new ProcessedPlayer(_footballer, _currentExplains, _previousData);
        FootballerScoreDetailElement diff = getPlayerDiff();
        if (_previousData != null) {
            diff = new DataFilter(_currentExplains, _previousData.rawData.explains, diff).filter();
        }
        addNewEvents(currentPlayerData.events, diff, _footballer, _currentExplains);

        return currentPlayerData;
    }

    private FootballerScoreDetailElement getPlayerDiff() {
        return _currentExplains.compare(_previousData != null ? _previousData.rawData.explains : null);
    }

    private static void addNewEvents(List<MatchEvent> diff, FootballerScoreDetailElement detailsDiff, Footballer footballer, FootballerScoreDetailElement currentDetail) {
        PlayerEventGenerator generator = new PlayerEventGenerator();
        List<MatchEvent> newEvents = generator.createNewEvents(detailsDiff, footballer, currentDetail);
        diff.addAll(newEvents);
    }
}
