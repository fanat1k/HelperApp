package com.pereginiak.helperapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HelperAppMainActivity extends AppCompatActivity {
    //private MyBroadcastReceiver receiver;
    //private IntentFilter intentFilter;

    private static final String GPS_TRACKER_PACKAGE_NAME = "com.kasian.trackme";
    private static final String GPS_TRACKER_MAIN_CLASS_NAME = GPS_TRACKER_PACKAGE_NAME + ".MainActivity";
    private static final String GPS_TRACKER_SERVICE_CLASS_NAME = GPS_TRACKER_PACKAGE_NAME + ".ServiceActivity";

    private static final String TAG = "HelperApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

/*
    public void clearTrackerLogView(View view) {
        TextView trackerLogView = getTrackerLogEditText();
        trackerLogView.setText("");
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String coordinates = data.getStringExtra("coordinates");
            if (coordinates != null) {
                coordinates = coordinates.substring(11);    // Truncate date, e.g. "2019-03-05;"
            }
            Log.i(TAG, "Received coordinates:\n" + coordinates);
            //getTrackerLogEditText().append(coordinates + "\n");
        } else {
            Log.e(TAG, "Can not get coordinates from gps tracker");
            //getTrackerLogEditText().append("ERROR: can not get coordinates\n");
        }
    }

    public void getGpsCoordinates(View view) {
        Intent intent = new Intent();
        intent.setClassName(GPS_TRACKER_PACKAGE_NAME, GPS_TRACKER_SERVICE_CLASS_NAME);

        Log.i(TAG,"run " + GPS_TRACKER_SERVICE_CLASS_NAME);
        startActivityForResult(intent, 1);
    }

    public void startGpsTrackerService(View view) {
        Intent service = new Intent();
        service.setClassName(GPS_TRACKER_PACKAGE_NAME, GPS_TRACKER_MAIN_CLASS_NAME);

        startActivityForResult(service, 1);

        // TODO: 02.03.2019 does nothing
        //startService(service);
        // TODO: 02.03.2019 does nothing
        //ContextCompat.startForegroundService(this, service);
    }

/*
    private TextView getTrackerLogEditText() {
        return this.findViewById(R.id.gpsTrackerLog);
    }
*/


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    // TODO: 02.03.2019 start NFC reader
    //intent.setClassName("com.pereginiak.gateway1c.nfc", "com.pereginiak.gateway1c.nfc.NfcReader");

    // TODO: 18.11.2018 does not work anything (in onCreate())
                trackerLogView.setFocusable(false);
                trackerLogView.setEnabled(false);
                trackerLogView.setCursorVisible(false);
                trackerLogView.setKeyListener(null);
                //trackerLogView.setBackgroundColor(Color.TRANSPARENT);


    public void startAnotherApp(View view) {
        //TODO(kasian @2018-06-24): working example
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("com.pereginiak.gateway1c");
        if (intent == null) {
            Log.intent(TAG, "Application is not installed:" + intent);
        } else {
            startActivity(intent);
        }

    //Does not work by some means:(
//        IntentFiltertent intent = new Intent(Intent.ACTION_RUN);
//        intent.setComponent(new ComponentName("com.pereginiak.gateway1c","com.pereginiak.gateway1c.MainActivity"));
//        startActivity(intent);
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

    private void listenGpsTracker() {
        TextView textView = getTrackerLogEditText();
        textView.setKeyListener(null);

        MyBroadcastReceiver receiver = new MyBroadcastReceiver(textView);
        IntentFilter intentFilter = new IntentFilter("com.kasian.LOCACTION_CHANGED");

        Log.i(TAG, "register MyBroadcastReceiver");
        registerReceiver(receiver, intentFilter);
    }
*/
}
