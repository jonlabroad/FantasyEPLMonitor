package com.jlabroad.eplfantasymatchtracker.aws;


import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

public class Credentials {
    public CognitoCachingCredentialsProvider creds;
    private static Credentials instance = null;

    protected Credentials() {
    }

    public static Credentials instance() {
        if(instance == null) {
            instance = new Credentials();
        }
        return instance;
    }

    public void initializeCognitoProvider(Context context) {
        // Initialize the Amazon Cognito credentials provider
        creds = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:5db4b823-1a8f-463d-bbb5-e552d0235549", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
    }
}
