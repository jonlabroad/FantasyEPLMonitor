package Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DeviceConfig {
    HashMap<String, Subscription> subscriptions = new HashMap<>();

    public void addSubscription(String deviceId, int teamId, String teamName) {
        Subscription subscription = subscriptions.get(deviceId);
        if (subscription == null) {
            subscription = new Subscription(deviceId);
            subscriptions.put(deviceId, subscription);
        }
        subscription.teamsByTeamId.put(teamId, new TeamSubscription(teamId, teamName));
    }

    public boolean isSubscribed(String deviceId, int teamId) {
        Subscription subscription = subscriptions.get(deviceId);
        if (subscription != null) {
            return subscription.teamsByTeamId.containsKey(teamId);
        }
        return false;
    }

    public HashSet<String> getSubscribers(int teamId) {
        HashSet<String> devices = new HashSet<>();
        for (Subscription sub : subscriptions.values()) {
            if (sub.teamsByTeamId.containsKey(teamId) && !devices.contains(sub.deviceId)) {
                devices.add(sub.deviceId);
            }
        }
        return devices;
    }

    public HashSet<Integer> getAllTeamIds() {
        HashSet<Integer> teams = new HashSet<>();
        for (Subscription sub : subscriptions.values()) {
            for (int teamId : sub.teamsByTeamId.keySet()) {
                teams.add(teamId);
            }
        }
        return teams;
    }
}
