package config;

import data.TeamIdName;

import java.util.HashMap;

public class CloudAppConfig {
    public int CurrentGameWeek;
    public HashMap<Integer, TeamIdName> AvailableTeams = new HashMap<>();
}
