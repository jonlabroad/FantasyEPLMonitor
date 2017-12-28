package client.Request;

import com.sun.org.apache.xpath.internal.operations.Variable;
import config.GlobalConfig;
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

    public HttpRequest GenerateBootstrapStaticRequest() {
        return Build(GlobalConfig.BootstrapStaticPath);
    }

    public HttpRequest GenerateLiveDataRequest(int eventId) {
        String resolvedUrl = VariableSubstitutor.Substitute(GlobalConfig.LivePath, 0, eventId);
        return Build(resolvedUrl);
    }

    public HttpRequest GeneratePicksRequest(int teamId, int eventId) {
        String resolvedUrl = VariableSubstitutor.Substitute(GlobalConfig.PicksPath, teamId, eventId);
        return Build(resolvedUrl);
    }

    public HttpRequest GenerateFootballerDetailRequest(int footballerId) {
        String resolvedUrl = VariableSubstitutor.SubstituteFootballerId(GlobalConfig.FootballerDetailsPath, footballerId);
        return Build(resolvedUrl);
    }

    private HttpRequest Build(String path) {
        return Unirest.get(GlobalConfig.EplBaseUrl + path);
    }
}
