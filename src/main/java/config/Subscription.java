package config;

import java.util.HashMap;

public class Subscription {
    String deviceId;
    HashMap<Integer, TeamSubscription> teamsByTeamId = new HashMap<>();

    public Subscription(String devId) {
        deviceId = devId;
    }
}
