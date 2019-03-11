package com.pereginiak.helperapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HelperAppMainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //private MyBroadcastReceiver receiver;
    //private IntentFilter intentFilter;

    private GoogleMap googleMap;

    private static final String GPS_TRACKER_PACKAGE_NAME = "com.kasian.trackme";
    private static final String GPS_TRACKER_MAIN_CLASS_NAME = GPS_TRACKER_PACKAGE_NAME + ".MainActivity";
    private static final String GPS_TRACKER_SERVICE_CLASS_NAME = GPS_TRACKER_PACKAGE_NAME + ".ServiceActivity";

    private static final String COORDINATES_DELIMITER = ";";
    private static final String TAG = "HelperApp";

    // TODO: 08.03.2019 sync access?
    private static LatLng lastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting reference to the SupportMapFragment of activity_main.xml
        //SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Getting GoogleMap object from the fragment
        //googleMap = fm.getMap();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //googleMap.setOnMyLocationButtonClickListener(this);
        //googleMap.setOnMyLocationClickListener(this);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
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
            List<LatLng> coordinates = fetchCoordinates(data);
            if (!coordinates.isEmpty()) {
                drawTrack(coordinates);
                saveLastPosition(coordinates);
            }
            //coordinates = coordinates.substring(11);    // Truncate date, e.g. "2019-03-05;"
            //getTrackerLogEditText().append(coordinates + "\n");
        } else {
            Log.e(TAG, "Can not get coordinates from gps tracker");
            Toast.makeText(this, "Can not get coordinates from gps tracker", Toast.LENGTH_SHORT).show();
            //getTrackerLogEditText().append("ERROR: can not get coordinates\n");
        }
    }

    private List<LatLng> fetchCoordinates(Intent data) {
        List<LatLng> coordinateList = new LinkedList<LatLng>();

        String coordinates = data.getStringExtra("coordinates");
        if (coordinates == null) {
            Toast.makeText(this, "TrackMe service is not running!", Toast.LENGTH_SHORT).show();
        } else {
            if (coordinates.isEmpty()) {
                Toast.makeText(this, "No new coordinates...", Toast.LENGTH_SHORT).show();
            } else {
                String[] lines = coordinates.split("\n");
                for (String line : lines) {
                    String[] str = line.split(COORDINATES_DELIMITER);
                    double latitude = Double.parseDouble(str[2]);
                    double longitude = Double.parseDouble(str[3]);

                    // TODO: 07.03.2019 "ping" coordinates
                    if (latitude == 0) {
                        Toast.makeText(this, "Received ping... ", Toast.LENGTH_SHORT).show();
                        //latitude = getRandomCoordinate(true);
                        //longitude = getRandomCoordinate(false);

                        // TODO: 08.03.2019 do not include "ping" coordinates into list
                        //LatLng coordinate = new LatLng(latitude, longitude);
                        //coordinateList.add(coordinate);
                    } else {
                        LatLng coordinate = new LatLng(latitude, longitude);
                        coordinateList.add(coordinate);
                    }
                }
            }
        }

        return coordinateList;
    }

    private double getRandomCoordinate(boolean isLatitude) {
        //double x = isLatitude ? 50.4546600 : 30.5238000;
        double x = isLatitude ? 50.3919 : 30.3683;

        int i = new Random().nextInt(10);
        double diff = (double) i / 10000;
        if (i < 5) {
            x -= diff;
        } else {
            x += diff;
        }
        return x;
    }

    private void drawTrack(List<LatLng> coordinates) {
        PolylineOptions opts = new PolylineOptions();

        if (lastPosition == null) {
            initMapPosition(coordinates.get(0).latitude, coordinates.get(0).longitude);
        } else {
            opts.add(lastPosition);
        }

        for (LatLng coordinate : coordinates) {
            opts.add(coordinate);
        }

        googleMap.addPolyline(opts.color(Color.RED));
    }

    private void saveLastPosition(List<LatLng> coordinates) {
        lastPosition = coordinates.get(coordinates.size() - 1);
    }
    
    private void initMapPosition(double latitude, double longitude) {
        LatLng startPosition = new LatLng(latitude, longitude);

        lastPosition = startPosition;
        googleMap.addMarker(new MarkerOptions().position(startPosition).title("Start"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPosition)      // Sets the center of the map to location user
                .zoom(16)                   // Sets the zoom
                .bearing(0)                 // Sets the orientation of the camera to north
                .tilt(0)                    // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void clearMap(View view) {
        googleMap.clear();
        lastPosition = null;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
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

/*
        double x = 50.4546600;
        double y = 30.5238000;
        for (int i = 0; i < 10; i++) {
            x += i * 0.1;
            y += i * 0.2;
            drawTrack(x, y);
        }
*/

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
