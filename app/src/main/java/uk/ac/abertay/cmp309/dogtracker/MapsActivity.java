package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnSuccessListener<Location> {

    private static final int LOCATION_REQUEST = 1;
    private MapView mapView;
    private GoogleMap mMap;
    private TextView textViewTimer;
    private Button startButton;

    private FusedLocationProviderClient fusedProviderClient;

    private Marker markerMyLocation;
    private List<LatLng> points;
    private Polyline polyline;
    private boolean recordWalk = false;
    private LocationAlarmHandler alarmHandler;

    private long startTime = 0;
    private long millis = 0;
    private int seconds = 0;
    private int minutes = 0;


    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            textViewTimer.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    public MapsActivity() {
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        textViewTimer = (TextView) findViewById(R.id.textViewTimeWalked);
        startButton = (Button) findViewById(R.id.buttonStartWalking);

        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter("GPSLocationUpdates"));

        alarmHandler = new LocationAlarmHandler(this);
        alarmHandler.cancelAlarmManager();
        alarmHandler.setAlarmManager();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onBackPressed() {
        cancelWalk();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST);
        }
        fusedProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedProviderClient.getLastLocation().addOnSuccessListener(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(false));

        points = polyline.getPoints();

        markerMyLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0))
                .title("My Position")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
        );
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            Log.i(Utils.TAG, "Location Found");
            updateMap(location);
        } else {
            Log.e(Utils.TAG, "Location Not Found");
        }
    }

    public void handleClicks(View view) {
        switch (view.getId()) {
            case R.id.buttonStartWalking:
                if (startButton.getText().equals("Cancel!")) {
                    cancelWalk();
                } else {
                    startWalk();
                }
                break;
            case R.id.buttonFinishWalking:
                finishWalk();
                break;
        }
    }

    private void cancelWalk() {
        //TODO: ADD POP UP TO ASK USER TO CONFIRM
        timerHandler.removeCallbacks(timerRunnable);
        alarmHandler.cancelAlarmManager();
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
        finish();
    }

    private void startWalk() {
        Toast.makeText(this, "Start Walking", Toast.LENGTH_SHORT).show();
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        startButton.setText("Cancel!");
        recordWalk = true;
    }

    private void finishWalk() {
        if (minutes > 0 || seconds > 0) {
            //TODO: Save polyline to database
            Toast.makeText(this, "Finished Walked", Toast.LENGTH_SHORT).show();
            timerHandler.removeCallbacks(timerRunnable);
            startButton.setText("Start!");
            alarmHandler.cancelAlarmManager();
            Intent intent = new Intent(this, LocationService.class);
            stopService(intent);
            //unregisterReceiver(locationReceiver);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("minutes", minutes);
            resultIntent.putExtra("seconds", seconds);
            Bundle poly = new Bundle();
            poly.putSerializable("polyline", (Serializable) points);
            resultIntent.putExtra("polyline", poly);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Please start the walk!", Toast.LENGTH_SHORT).show();
        }
    }




    public void updateMap(Location location) {
            if (mMap != null) {
                Log.i(Utils.TAG, "Updating Map");
                if (recordWalk) {
                    points.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    polyline.setPoints(points);
                }
                Log.i(Utils.TAG, "LatLng: " + new LatLng(location.getLatitude(), location.getLongitude()));
                markerMyLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }
    }

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            Bundle b = intent.getBundleExtra("Location");
            Location lastKnownLoc = (Location) b.getParcelable("Location");
            if (lastKnownLoc != null) {
                updateMap(lastKnownLoc);
            }

        }
    };
}