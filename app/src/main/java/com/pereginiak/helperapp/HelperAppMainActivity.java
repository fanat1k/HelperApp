package com.pereginiak.helperapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HelperAppMainActivity extends AppCompatActivity {

    //private MyBroadcastReceiver receiver;
    //private IntentFilter intentFilter;

    private static final String TAG = "HelperApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clickButton = (Button) findViewById(R.id.clearButton);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView trackerLogView = getTrackerLogEditText();
                trackerLogView.setText("");

/*
    // TODO: 18.11.2018 does not work anything
                trackerLogView.setFocusable(false);
                trackerLogView.setEnabled(false);
                trackerLogView.setCursorVisible(false);
                trackerLogView.setKeyListener(null);
                //trackerLogView.setBackgroundColor(Color.TRANSPARENT);
*/
            }
        });

        //listenGpsTracker();
    }

/*
    private void listenGpsTracker() {
        TextView textView = getTrackerLogEditText();
        textView.setKeyListener(null);

        MyBroadcastReceiver receiver = new MyBroadcastReceiver(textView);
        IntentFilter intentFilter = new IntentFilter("com.kasian.LOCACTION_CHANGED");

        Log.i(TAG, "register MyBroadcastReceiver");
        registerReceiver(receiver, intentFilter);
    }
*/

    private TextView getTrackerLogEditText() {
        return this.findViewById(R.id.gpsTrackerLog);
    }

    public void getGpsCoordinates(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.kasian.trackme", "com.kasian.trackme.ServiceActivity");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String coordinates = data.getStringExtra("coordinates");
            Log.i(TAG, "Get coordinates:" + coordinates);
            getTrackerLogEditText().append(coordinates + "\n");
        } else {
            Log.e(TAG, "Can't get coordinates from gps tracker");
            getTrackerLogEditText().append("ERROR: can't get coordinates\n");
        }
    }

    // TODO: 18.11.2018 use it
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();

        String time = intent.getStringExtra("time");
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        String logLine = time.split(" ")[1] + " - " + latitude + " : " + longitude;

        Log.i(TAG, "location changed:" + logLine);
        getTrackerLogEditText().append(" " + logLine + "\n");
    }


    //TODO(kasian @2018-09-22):
    private void startAnotherApp (View view) {
        Intent intent = new Intent();
        //intent.setClassName("com.pereginiak.gateway1c.nfc", "com.pereginiak.gateway1c.nfc.NfcReader");
        intent.setClassName("com.pereginiak.trackme", "com.pereginiak.trackme.TrackMeMainActivity");
        startActivityForResult(intent, 1);
/*
        //TODO(kasian @2018-06-24): working example
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("com.pereginiak.gateway1c");
        if (intent == null) {
            Log.intent(TAG, "Application is not installed:" + intent);
        } else {
            startActivity(intent);
        }
*/

/*
        Does not work by some means:(
        Intent intent = new Intent(Intent.ACTION_RUN);
        intent.setComponent(new ComponentName("com.pereginiak.gateway1c","com.pereginiak.gateway1c.MainActivity"));
        startActivity(intent);
*/
    }

    private void startGPSTrackerService(View view) {
        //Intent service = new Intent("com.pereginiak.trackme.GPSTracker");

        Intent service = new Intent();
        service.setClassName("com.pereginiak.trackme", "com.pereginiak.trackme.GPSTracker");

        ContextCompat.startForegroundService(this, service);
        //startService(service);
    }
}
