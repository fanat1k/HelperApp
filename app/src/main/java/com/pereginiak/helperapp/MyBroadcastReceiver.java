package com.pereginiak.helperapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private TextView trackerLog;

    public MyBroadcastReceiver() {
    }

    public MyBroadcastReceiver(TextView trackerLog) {
        this.trackerLog = trackerLog;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();

        String time = intent.getStringExtra("time");
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        String logLine = time + " - " + latitude + " : " + longitude;

        trackerLog.append(" " + logLine + "\n");
    }
}
