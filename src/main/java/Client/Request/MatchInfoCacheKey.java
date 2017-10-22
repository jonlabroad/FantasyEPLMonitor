package Client.Request;

public class MatchInfoCacheKey {
    private int _leagueId;
    private int _teamId;
    private int _gameweek;

    public MatchInfoCacheKey(int leagueId, int teamId, int gameweek) {
        _leagueId = leagueId;
        _teamId = teamId;
        _gameweek = gameweek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchInfoCacheKey that = (MatchInfoCacheKey) o;

        if (_leagueId != that._leagueId) return false;
        if (_teamId != that._teamId) return false;
        return _gameweek == that._gameweek;
    }

    @Override
    public int hashCode() {
        int result = _leagueId;
        result = 31 * result + _teamId;
        result = 31 * result + _gameweek;
        return result;
    }
}
