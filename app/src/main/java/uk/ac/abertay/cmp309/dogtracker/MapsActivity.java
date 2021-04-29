package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private LocationCallback locationCallback;

    private Marker markerMyLocation;

    private long startTime = 0;
    private long millis = 0;
    private int seconds = 0;
    private int minutes = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle mapViewBundle = null;
        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        textViewTimer = (TextView)findViewById(R.id.textViewTimeWalked);
        startButton = (Button)findViewById(R.id.buttonStartWalking);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if(mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if(locationResult != null) {
                    Log.d(Utils.TAG, locationResult.getLastLocation().toString());
                    if(mMap != null) {
                        double lat = locationResult.getLastLocation().getLatitude();
                        double lng = locationResult.getLastLocation().getLongitude();
                        LatLng myLatLng = new LatLng(lat, lng);
                        markerMyLocation.setPosition(myLatLng);
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                    }
                }
            }
        };

        boolean checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(fusedProviderClient != null && checkResult) {
            fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        LatLng abertay = new LatLng(56.4633, -2.9739);
        mMap.addMarker(new MarkerOptions().position(abertay).title("Abertay University"));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(abertay));

        markerMyLocation = mMap.addMarker(new MarkerOptions().position(abertay).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        startButton.setText("Start!");
        if(fusedProviderClient != null && locationCallback != null) {
            fusedProviderClient.removeLocationUpdates(locationCallback);
        }
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
        switch(view.getId()) {
            case R.id.buttonStartWalking:
                Toast.makeText(this, "Start Walking", Toast.LENGTH_SHORT).show();
                if(startButton.getText().equals("Stop!")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    startButton.setText("Start!");
                    Toast.makeText(this, "Time is: " + minutes + "min " + seconds + "sec", Toast.LENGTH_SHORT).show();
                }
                else {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    startButton.setText("Stop!");
                }
                break;
            case R.id.buttonFinishWalking:
                Toast.makeText(this, "Finished Walked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSuccess(Location location) {
        if(location != null) {

        }
    }

}