package com.jlabroad.eplfantasymatchtracker.notification;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MatchDataAlertReceiver extends FirebaseMessagingService {
    public static String EPL_FIREBASE_DATA_MESSAGE = "com.jlabroad.eplfantasymatchtracker.notification.MatchDataAlertReceiver.MATCH_DATA";

    private LocalBroadcastManager _broadcast;

    @Override
    public void onCreate() {
        _broadcast = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.format("Received message\n");
        Intent intent = new Intent(EPL_FIREBASE_DATA_MESSAGE);
        _broadcast.sendBroadcast(intent);
    }
}
