package com.jlabroad.eplfantasymatchtracker.notification;

import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jlabroad.eplfantasymatchtracker.aws.ApplicationEndpointRegister;
import com.jlabroad.eplfantasymatchtracker.aws.Credentials;

import static android.content.ContentValues.TAG;

public class EPLFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        System.out.println("WE GOT A TOKEN YAY");
        //sendRegistrationToServer(refreshedToken);
        String deviceId = FirebaseInstanceId.getInstance().getId();
        String token = FirebaseInstanceId.getInstance().getToken();
        while (token == null) {
            System.out.println("WTF");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            token = FirebaseInstanceId.getInstance().getToken();
        }
        ApplicationEndpointRegister endpointRegister = new ApplicationEndpointRegister(
                getUniqueDeviceId(),
                deviceId,
                FirebaseInstanceId.getInstance().getToken(), Credentials.instance().creds);
        endpointRegister.register();

    }

    private  String getUniqueDeviceId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
