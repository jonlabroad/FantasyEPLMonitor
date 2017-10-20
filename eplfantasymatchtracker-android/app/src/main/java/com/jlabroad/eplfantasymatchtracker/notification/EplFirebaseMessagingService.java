package com.jlabroad.eplfantasymatchtracker.notification;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class EplFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println(String.format("Hey cool a message: %s", remoteMessage.getData()));
    }
}
