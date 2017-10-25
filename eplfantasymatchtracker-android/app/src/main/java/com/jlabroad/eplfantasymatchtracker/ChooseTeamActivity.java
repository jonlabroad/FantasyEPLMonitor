package com.jlabroad.eplfantasymatchtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jlabroad.eplfantasymatchtracker.config.DeviceConfigurator;
import com.jlabroad.eplfantasymatchtracker.config.GlobalConfig;

public class ChooseTeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MatchView.TEAM_CHOOSE_REQUEST);

        // JUST FOR TESTING THE FLOW
        GlobalConfig.deviceConfig.addSubscription(GlobalConfig.deviceConfig.deviceId, 2365803, "The Vardy Boys");
        new DeviceConfigurator().writeConfig(GlobalConfig.deviceConfig, GlobalConfig.deviceConfig.deviceId);

        Intent backToMain = new Intent(getApplicationContext(), MatchView.class);
        startActivity(backToMain);
    }
}