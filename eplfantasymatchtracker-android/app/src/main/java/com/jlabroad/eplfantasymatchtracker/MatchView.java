package com.jlabroad.eplfantasymatchtracker;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.jlabroad.eplfantasymatchtracker.Data.MatchInfo;
import com.jlabroad.eplfantasymatchtracker.Data.Team;
import com.jlabroad.eplfantasymatchtracker.notification.FCMClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MatchView extends AppCompatActivity {

    CognitoCachingCredentialsProvider _credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        // TODO Get rid of this and do it the right way (async network)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initializeCognitoProvider();
        FCMClient client = new FCMClient();
        System.out.println(String.format("Token: %s\n", client.GetToken()));
    }

    @Override
    public void onStart() {
        try {
            String bucketData = readBucket();
            MatchInfo matchInfo = new Gson().fromJson(bucketData, MatchInfo.class);
            printMatchInfo(matchInfo);
            System.out.println(bucketData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    protected void initializeCognitoProvider() {
        // Initialize the Amazon Cognito credentials provider
        _credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:5db4b823-1a8f-463d-bbb5-e552d0235549", // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_EAST_1, // Region
                _credentialsProvider);

        // Create a record in a dataset and synchronize with the server
        Dataset dataset = syncClient.openOrCreateDataset("eplMatchTrackerAndroid");
        dataset.put("myKey", "myValue");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
            }
        });
    }

    protected String readBucket() throws IOException {
        AmazonS3 s3 = new AmazonS3Client(_credentialsProvider);
        S3Object object = s3.getObject("fantasyeplmatchtracker", "MatchInfo_1326527_8");
        InputStream objectData = object.getObjectContent();
        String retString = readTextInputStream(objectData);
        objectData.close();

        return retString;
    }

    private static String readTextInputStream(InputStream input)
            throws IOException {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String retLine = "";
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;

            retLine += line + "\n";
        }
        return retLine;
    }

    private void printMatchInfo(MatchInfo info) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.livescorelayout);
        linearLayout.removeAllViewsInLayout();
        ArrayList<TextView> textViews = new ArrayList<>();
        TextView textView = new TextView(this);

        Integer[] teamIds = info.teams.keySet().toArray(new Integer[info.teams.keySet().size()]);
        textView.setText(String.format("%s (W%d-L%d-D%d) vs %s (W%d-L%d-D%d)",
                info.match.entry_1_name,
                info.teams.get(teamIds[0]).standing.matches_won,
                info.teams.get(teamIds[0]).standing.matches_lost,
                info.teams.get(teamIds[0]).standing.matches_drawn,
                info.match.entry_2_name,
                info.teams.get(teamIds[1]).standing.matches_won,
                info.teams.get(teamIds[1]).standing.matches_lost,
                info.teams.get(teamIds[1]).standing.matches_drawn));
        textViews.add(textView);

        textView = new TextView(this);
        textView.setText(String.format("%d (%d) - %d (%d)",
                info.teams.get(teamIds[0]).currentPoints.startingScore,
                info.teams.get(teamIds[0]).currentPoints.subScore,
                info.teams.get(teamIds[1]).currentPoints.startingScore,
                info.teams.get(teamIds[1]).currentPoints.subScore));
        textViews.add(textView);
        for (TextView v : textViews) {
            linearLayout.addView(v);
        }
    }
}
