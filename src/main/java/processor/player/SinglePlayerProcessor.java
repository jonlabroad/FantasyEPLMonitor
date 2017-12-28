package processor.player;

import client.DataFilter;
import data.MatchEvent;
import data.ProcessedPlayer;
import data.eplapi.*;
import org.joda.time.DateTime;
import util.Date;

import java.util.List;

public class SinglePlayerProcessor {
    int _gameweek;
    Footballer _footballer;
    FootballerScoreDetailElement _currentExplains;
    Live _currentLiveData;
    ProcessedPlayer _previousData;
    ProcessedPlayerProvider _playerProvider;

    public SinglePlayerProcessor(ProcessedPlayerProvider playerProvider, int gameweek, Footballer footballer, FootballerScoreDetailElement explains, Live liveData) {
        _footballer = footballer;
        _currentExplains = explains;
        _currentLiveData = liveData;
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
        determineFixtureStatus(currentPlayerData);
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

    private void determineFixtureStatus(ProcessedPlayer player) {
        player.isCurrentlyPlaying = false;
        player.isDonePlaying = false;
        int realTeamId = _footballer.team;
        for (Fixture fixture : _currentLiveData.fixtures) {
            if (fixture.team_a == realTeamId || fixture.team_h == realTeamId) {
                if (isFixtureInProgress(fixture)) {
                    player.isCurrentlyPlaying = true;
                }
                else if (isFixtureComplete(fixture)) {
                    player.isDonePlaying = true;
                }
            }
        }
    }

    private boolean isFixtureInProgress(Fixture fixture) {
        return !fixture.finished && fixture.started;
    }

    private boolean isFixtureComplete(Fixture fixture) {
        return fixture.finished;
    }
}
