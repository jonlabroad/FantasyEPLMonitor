package config;

import java.util.HashMap;
import java.util.HashSet;

public class DeviceConfig {
    public String uniqueDeviceId;
    public Subscription subscriptions = new Subscription();

    public DeviceConfig(String uId) {
        uniqueDeviceId = uId;
    }

    public void addSubscription(int teamId, String teamName) {
        subscriptions.teamsByTeamId.put(teamId, new TeamSubscription(teamId, teamName));
    }

    public boolean isSubscribed(int teamId) {
        return subscriptions.teamsByTeamId.containsKey(teamId);
    }

    public HashSet<String> getSubscribers(int teamId) {
        HashSet<String> devices = new HashSet<>();
        if (subscriptions.teamsByTeamId.containsKey(teamId) && !devices.contains(uniqueDeviceId)) {
            devices.add(uniqueDeviceId);
        }
        return devices;
    }

    public HashSet<Integer> getAllTeamIds() {
        HashSet<Integer> teams = new HashSet<>();
        for (int teamId : subscriptions.teamsByTeamId.keySet()) {
            teams.add(teamId);
        }
        return teams;
    }
}
