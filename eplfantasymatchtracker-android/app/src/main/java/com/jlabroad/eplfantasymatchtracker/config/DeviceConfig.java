package com.jlabroad.eplfantasymatchtracker.config;

import java.util.HashSet;

public class DeviceConfig {
    public String deviceId;
    public Subscription subscriptions = new Subscription();

    public DeviceConfig(String devId) {
        deviceId = devId;
    }

    public void addSubscription(String deviceId, int teamId, String teamName) {
        subscriptions.teamsByTeamId.put(teamId, new TeamSubscription(teamId, teamName));
    }

    public boolean isSubscribed(String deviceId, int teamId) {
        return subscriptions.teamsByTeamId.containsKey(teamId);
    }

    public HashSet<String> getSubscribers(int teamId) {
        HashSet<String> devices = new HashSet<>();
        if (subscriptions.teamsByTeamId.containsKey(teamId) && !devices.contains(deviceId)) {
            devices.add(deviceId);
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

