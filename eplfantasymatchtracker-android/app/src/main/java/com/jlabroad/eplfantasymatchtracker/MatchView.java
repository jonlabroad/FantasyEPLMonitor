package com.jlabroad.eplfantasymatchtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.jlabroad.eplfantasymatchtracker.config.GlobalConfig;
import com.jlabroad.eplfantasymatchtracker.data.MatchInfo;
import com.jlabroad.eplfantasymatchtracker.data.Team;
import com.jlabroad.eplfantasymatchtracker.aws.ApplicationEndpointRegister;
import com.jlabroad.eplfantasymatchtracker.aws.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.jlabroad.eplfantasymatchtracker.notification.MatchDataAlertReceiver.EPL_FIREBASE_DATA_MESSAGE;

public class MatchView extends AppCompatActivity {
    private int _gameweek = 9;
    private int _teamId = 2365803; //me
    //private int _teamId = 1326527; //ryan
    CognitoCachingCredentialsProvider _credentialsProvider;
    BroadcastReceiver _matchAlertDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        // TODO Get rid of this and do it the right way (async network)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        registerDevice();
        createUpdateReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToUpdates();
        readLatestData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                readLatestData();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_action_menu, menu);
        return true;
    }

    public void readLatestData() {
        try {
            String bucketData = readBucket();
            MatchInfo matchInfo = new Gson().fromJson(bucketData, MatchInfo.class);
            printMatchInfo(matchInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String readBucket() throws IOException {
        AmazonS3 s3 = new AmazonS3Client(_credentialsProvider);
        S3Object object = s3.getObject(GlobalConfig.S3Bucket, String.format("MatchInfo_%d_%d", _teamId, _gameweek));
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

        int team1Id = info.teamIds.get(0);
        int team2Id = info.teamIds.get(1);
        Team team1 = info.teams.get(team1Id);
        Team team2 = info.teams.get(team2Id);
        textView.setText(String.format("%s (W%d-L%d-D%d) vs %s (W%d-L%d-D%d)",
                team1.name,
                team1.standing.matches_won,
                team1.standing.matches_lost,
                team1.standing.matches_drawn,
                team2.name,
                team2.standing.matches_won,
                team2.standing.matches_lost,
                team2.standing.matches_drawn));
        textViews.add(textView);

        textView = new TextView(this);
        textView.setText(String.format("%d (%d) - %d (%d)",
                team1.currentPoints.startingScore,
                team1.currentPoints.subScore,
                team2.currentPoints.startingScore,
                team2.currentPoints.subScore));
        textViews.add(textView);

        for (String event : info.matchEvents) {
            textView = new TextView(this);
            textView.setText(event);
            textViews.add(textView);
        }

        for (TextView v : textViews) {
            linearLayout.addView(v);
        }
    }

    private void createUpdateReceiver() {
        _matchAlertDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readLatestData();
            }
        };
    }

    private void subscribeToUpdates() {
        LocalBroadcastManager.getInstance(this).registerReceiver((_matchAlertDataReceiver),
                new IntentFilter(EPL_FIREBASE_DATA_MESSAGE)
        );
    }

    private void registerDevice() {
        _credentialsProvider = Credentials.initializeCognitoProvider(getApplicationContext());
        ApplicationEndpointRegister endpointRegister = new ApplicationEndpointRegister(FirebaseInstanceId.getInstance().getId(),
                FirebaseInstanceId.getInstance().getToken(), _credentialsProvider);
        endpointRegister.register(_teamId);
    }
}
