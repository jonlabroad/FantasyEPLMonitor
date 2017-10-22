package com.jlabroad.eplfantasymatchtracker.aws;


import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

public class Credentials {
    public static CognitoCachingCredentialsProvider initializeCognitoProvider(Context context) {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:5db4b823-1a8f-463d-bbb5-e552d0235549", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        return credentialsProvider;
    }
}
