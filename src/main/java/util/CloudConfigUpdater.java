package util;

import client.EPLClient;
import client.EPLClientFactory;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import data.eplapi.BootstrapStatic;
import data.eplapi.Event;

public class CloudConfigUpdater {
    private EPLClient _client;

    public CloudConfigUpdater(EPLClient client) {
        _client = client != null ? client : EPLClientFactory.createClient();
    }

    public boolean update() {
        Event currentEvent = getCurrentEvent();
        boolean generateScoutingReports = true;
        if (currentEvent == null) {
            System.out.println("Unable to find current event");
            return false;
        }

        if (currentEvent.id != GlobalConfig.CloudAppConfig.CurrentGameWeek) {
            GlobalConfig.CloudAppConfig.CurrentGameWeek = currentEvent.id;
            new CloudAppConfigProvider().write(GlobalConfig.CloudAppConfig);
            return true;
        }
        return false;
    }

    private Event getCurrentEvent() {
        BootstrapStatic boot = _client.getBootstrapStatic();
        int currentEvent = boot.currentEvent;
        for (Event event : boot.events) {
            if (event.id == currentEvent) {
                return event;
            }
        }
        return null;
    }
}
