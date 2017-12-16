
package client;

import data.eplapi.FootballerDetails;
import data.eplapi.FootballerScoreDetailElement;

public class DataFilter {
    FootballerScoreDetailElement _currentExplain;
    FootballerScoreDetailElement _previousExplain;
    FootballerScoreDetailElement _diff;

    public DataFilter(FootballerDetails currentDetails, FootballerDetails previousDetails, FootballerScoreDetailElement diff) {
        _currentExplain = getExplain(currentDetails);
        _previousExplain = getExplain(previousDetails);
        _diff = diff;
    }

    public void filter() {
        if (_previousExplain == null || _currentExplain == null || _diff == null) {
            return;
        }
        ignoreOlderData();
    }

    public void ignoreOlderData() {
        if (_diff.minutes.value < 0 && _diff.minutes.value > -45) {
            System.out.format("Found older data. (%d -> %d) Min played diff: %d\n",
                    _previousExplain.minutes.value,
                    _currentExplain.minutes,
                    _diff.minutes.value);
            _currentExplain = _previousExplain;
            _diff = _currentExplain.Compare(_currentExplain);
        }
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
