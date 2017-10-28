package com.jlabroad.eplfantasymatchtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.jlabroad.eplfantasymatchtracker.config.DeviceConfig;
import com.jlabroad.eplfantasymatchtracker.config.DeviceConfigurator;
import com.jlabroad.eplfantasymatchtracker.config.GlobalConfig;
import com.jlabroad.eplfantasymatchtracker.data.MatchEvent;
import com.jlabroad.eplfantasymatchtracker.data.MatchEventType;
import com.jlabroad.eplfantasymatchtracker.data.MatchInfo;
import com.jlabroad.eplfantasymatchtracker.data.Team;
import com.jlabroad.eplfantasymatchtracker.aws.ApplicationEndpointRegister;
import com.jlabroad.eplfantasymatchtracker.aws.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import static com.jlabroad.eplfantasymatchtracker.notification.MatchDataAlertReceiver.EPL_FIREBASE_DATA_MESSAGE;

public class MatchView extends AppCompatActivity {
    public static final String TEAM_CHOOSE_REQUEST = "com.jlabroad.eplfantasymatchtracker.TEAM_CHOOSE_REQUEST";

    private int _gameweek = 10;
    private int _teamId;
    BroadcastReceiver _matchAlertDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        // TODO Get rid of this and do it the right way (async network)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            registerDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO really gotta make this better!
        if (GlobalConfig.deviceConfig.getAllTeamIds().isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);
            intent.putExtra(TEAM_CHOOSE_REQUEST, "testtesttest");
            startActivity(intent);
        } else {
            getTeamId();
            createUpdateReceiver();
            subscribeToUpdates();
            readLatestData();
        }
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

    private void getTeamId() {
        DeviceConfig config = GlobalConfig.deviceConfig;
        HashSet<Integer> teamIds = config.getAllTeamIds();

        //Just pick the first, we only support one subscription at this time
        _teamId = teamIds.toArray(new Integer[0])[0];
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
        AmazonS3 s3 = new AmazonS3Client(Credentials.instance().creds);
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
        int team1Id = info.teamIds.get(0);
        int team2Id = info.teamIds.get(1);
        Team team1 = info.teams.get(team1Id);
        Team team2 = info.teams.get(team2Id);

        TextView team1Name = (TextView) findViewById(R.id.team1name);
        TextView team2Name = (TextView) findViewById(R.id.team2name);
        team1Name.setText(team1.name);
        team2Name.setText(team2.name);

        TextView team1Record = (TextView) findViewById(R.id.team1record);
        TextView team2Record = (TextView) findViewById(R.id.team2record);
        team1Record.setText(String.format("W%d-L%d-D%d",
                team1.standing.matches_won,
                team1.standing.matches_lost,
                team1.standing.matches_drawn));
        team2Record.setText(String.format("W%d-L%d-D%d",
                team2.standing.matches_won,
                team2.standing.matches_lost,
                team2.standing.matches_drawn));

        TextView scoreLine = (TextView) findViewById(R.id.scoreText);
        scoreLine.setText(String.format("(%d) %d - %d (%d)",
                team1.currentPoints.subScore, team1.currentPoints.startingScore,
                team2.currentPoints.startingScore, team2.currentPoints.subScore));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.livescorelayout);
        linearLayout.removeAllViewsInLayout();
        ArrayList<TextView> textViews = new ArrayList<>();
        for (MatchEvent event : info.matchEvents) {
            TextView textView = new TextView(this);
            textView.setText(eventToString(event));
            textView.setBackgroundResource(event.teamId == _teamId ? R.color.myteamcolor : R.color.otherteamcolor);
            textViews.add(textView);
        }

        for (TextView v : textViews) {
            linearLayout.addView(v);
        }
    }

    private String eventToString(MatchEvent event) {
        if (isEnumerated(event.type)) {
            return String.format("%s: %d %s %s (%d)", event.dateTime, event.number, event.typeToReadableString(), event.footballer.web_name, event.pointDifference);
        }
        else {
            return String.format("%s: %s %s (%d)", event.dateTime, event.typeToReadableString(), event.footballer.web_name, event.pointDifference);
        }
    }

    private boolean isEnumerated(MatchEventType type) {
        return  type == MatchEventType.GOAL ||
                type == MatchEventType.ASSIST ||
                type == MatchEventType.MINUTES_PLAYED ||
                type == MatchEventType.YELLOW_CARD ||
                type == MatchEventType.RED_CARD ||
                type == MatchEventType.PENALTY_MISS ||
                type == MatchEventType.GOALS_CONCEDED ||
                type == MatchEventType.SAVES ||
                type == MatchEventType.PENALTY_SAVES ||
                type == MatchEventType.OWN_GOALS;
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

    private void registerDevice() throws IOException {
        Credentials.instance().initializeCognitoProvider(getApplicationContext());
        String deviceId = FirebaseInstanceId.getInstance().getId();
        ApplicationEndpointRegister endpointRegister = new ApplicationEndpointRegister(deviceId,
                FirebaseInstanceId.getInstance().getToken(), Credentials.instance().creds);
        endpointRegister.register();

        GlobalConfig.deviceConfig = new DeviceConfigurator().readConfig(deviceId);
    }
}
