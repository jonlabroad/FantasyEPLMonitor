package processor.player;

import client.DataFilter;
import data.MatchEvent;
import data.ProcessedPlayer;
import data.eplapi.*;
import org.joda.time.DateTime;
import util.Date;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerProcessor {
    int _gameweek;
    Footballer _footballer;
    ArrayList<FootballerScoreDetailElement> _currentExplains;
    Live _currentLiveData;
    ProcessedPlayer _previousData;
    ProcessedPlayerProvider _playerProvider;

    public SinglePlayerProcessor(ProcessedPlayerProvider playerProvider, int gameweek, Footballer footballer, ArrayList<FootballerScoreDetailElement> explains, Live liveData) {
        _footballer = footballer;
        _currentExplains = explains;
        _currentLiveData = liveData;
        _gameweek = gameweek;

        _playerProvider = playerProvider;
        _previousData = _playerProvider.getPlayer(footballer.id);
    }

    public ProcessedPlayer process() {
        if (_currentExplains == null) {
            System.out.format("No explains: %s\n", _footballer.web_name);
            return _previousData;
        }

        ProcessedPlayer currentPlayerData = new ProcessedPlayer(_footballer, _currentExplains, _previousData);
        determineFixtureStatus(currentPlayerData);
        ArrayList<FootballerScoreDetailElement> prevElements = _previousData != null ?_previousData.rawData.explains : new ArrayList<>();
        for (int i = 0; i < _currentExplains.size(); i++) {
            FootballerScoreDetailElement currExplain = _currentExplains.get(i);
            FootballerScoreDetailElement prevExplain = i < prevElements.size() ? prevElements.get(i) : null;
            FootballerScoreDetailElement diff = getPlayerDiff(currExplain, prevExplain);

            if (prevExplain != null) {
                DataFilter filter = new DataFilter(currExplain, prevExplain, diff);
                diff = filter.filter();
            }
            addNewEvents(currentPlayerData.events, diff, _footballer, currExplain);
        }

        return currentPlayerData;
    }

    private FootballerScoreDetailElement getPlayerDiff(FootballerScoreDetailElement currentExplain, FootballerScoreDetailElement prevExplain) {
        return currentExplain.compare(prevExplain != null ? prevExplain : null);
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
