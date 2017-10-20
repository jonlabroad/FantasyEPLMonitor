package com.jlabroad.eplfantasymatchtracker.notification;


import com.google.firebase.iid.FirebaseInstanceId;

public class FCMClient {
    public FCMClient() {

    }

    public String GetToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }
}
