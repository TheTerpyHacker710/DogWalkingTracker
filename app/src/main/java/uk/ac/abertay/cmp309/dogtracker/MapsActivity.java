package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnSuccessListener<Location> {

    private static final int LOCATION_REQUEST = 1;
    private MapView mapView;
    private GoogleMap mMap;
    private TextView textViewTimer;
    private Button startButton;

    private FusedLocationProviderClient fusedProviderClient;

    private Marker markerMyLocation;
    private List<LatLng> points;
    private List<List<LatLng>> listOfLines;
    private Polyline polyline;
    private boolean recordWalk = false;

    private long startTime = 0;
    private long millis = 0;
    private int seconds = 0;
    private int minutes = 0;

    private PowerManager.WakeLock wakeLock;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
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


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLockTag");

        LocalBroadcastManager.getInstance(this).registerReceiver(locationReciever, new IntentFilter("GPSLocationUpdates"));

        Intent intent = new Intent(getBaseContext(), LocationService.class);
        startService(intent);
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
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
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
        //timerHandler.removeCallbacks(timerRunnable);
        //startButton.setText("Start!");
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

    public void handleClicks(View view) {
        switch (view.getId()) {
            case R.id.buttonStartWalking:
                if (startButton.getText().equals("Cancel!")) {
                    //TODO: ADD POP UP TO ASK USER TO CONFIRM
                    timerHandler.removeCallbacks(timerRunnable);
                    Intent intent = new Intent(this, LocationService.class);
                    stopService(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Start Walking", Toast.LENGTH_SHORT).show();
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    startButton.setText("Cancel!");
                    recordWalk = true;
                }
                break;
            case R.id.buttonFinishWalking:
                if (minutes > 0 || seconds > 0) {
                    Toast.makeText(this, "Finished Walked", Toast.LENGTH_SHORT).show();
                    timerHandler.removeCallbacks(timerRunnable);
                    startButton.setText("Start!");
                    //listOfLines.add(points);
                    Intent intent = new Intent(this, LocationService.class);
                    stopService(intent);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("minutes", minutes);
                    resultIntent.putExtra("seconds", seconds);
                    //resultIntent.putExtra("polyline", points.toArray());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Please start the walk!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    private BroadcastReceiver locationReciever = new BroadcastReceiver() {
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