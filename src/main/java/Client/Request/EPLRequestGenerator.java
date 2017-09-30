package Client.Request;

import Config.GlobalConfig;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

public class EPLRequestGenerator {
    public HttpRequest GenerateLeagueH2hStandingsRequest(int leagueId) {
        String resolvedLeaguePath = VariableSubstitutor.SubstituteLeague(GlobalConfig.LeagueH2hPath, leagueId);
        return Build(resolvedLeaguePath);
    }

    public HttpRequest GenerateFootballersRequest() {
        return Build(GlobalConfig.FootballersPath);
    }

    public HttpRequest GeneratePicksRequest(int teamId, int eventId) {
        String resolvedUrl = VariableSubstitutor.Substitute(GlobalConfig.PicksPath, teamId, eventId);
        return Build(resolvedUrl);
    }

    private HttpRequest Build(String path) {
        return Unirest.get(GlobalConfig.EplBaseUrl + path);
    }
}
