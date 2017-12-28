
package client;

import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetailElement;

public class DataFilter {
    FootballerScoreDetailElement _currentExplain;
    FootballerScoreDetailElement _previousExplain;
    FootballerScoreDetailElement _diff;

    public DataFilter(FootballerScoreDetailElement currentExplain, FootballerScoreDetailElement previousExplain, FootballerScoreDetailElement diff) {
        _currentExplain = currentExplain;
        _previousExplain = previousExplain;
        _diff = diff;
    }

    public FootballerScoreDetailElement filter() {
        if (_previousExplain == null || _currentExplain == null || _diff == null) {
            return _diff;
        }
        FootballerScoreDetailElement newDiff = ignoreOlderData();
        return newDiff;
    }

    public FootballerScoreDetailElement ignoreOlderData() {
        if (_diff.minutes.value < 0 && _diff.minutes.value > -45) {
            System.out.format("Found older data. (%d -> %d) Min played diff: %d\n",
                    _previousExplain.minutes.value,
                    _currentExplain.minutes.value,
                    _diff.minutes.value);
            _currentExplain.set(_previousExplain);
            return _currentExplain.compare(_currentExplain);
        }
        return _diff;
    }

    private void removeBackwardsMinutes() {
        if (_diff.minutes.value < 0) {
            _currentExplain.minutes = _previousExplain.minutes;
            _diff.minutes = _currentExplain.minutes.diff(_previousExplain.minutes);
        }
    }

    private FootballerScoreDetailElement getExplain(FootballerDetails details) {
        if (details != null) {
            return details.explain[0].explain;
        }
        return null;
    }
}
