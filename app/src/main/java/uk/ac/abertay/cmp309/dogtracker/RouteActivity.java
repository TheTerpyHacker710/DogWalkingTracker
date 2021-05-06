package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import uk.ac.abertay.cmp309.dogtracker.ui.walking.WalkingViewModel;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, OnSuccessListener<Location> {

    private static final int LOCATION_REQUEST = 1;

    private MapView mapView;
    private GoogleMap mMap;
    private TextView textViewHoursWalked;
    private TextView textViewDistanceWalked;

    private final DecimalFormat df = new DecimalFormat("0.00");

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapViewRoutes);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        textViewHoursWalked = (TextView) findViewById(R.id.textViewHoursWalkedTodayRoutesValue);
        textViewDistanceWalked = (TextView) findViewById(R.id.textViewDistanceWalkedTodayRoutesValue);
        Button buttonBack = (Button) findViewById(R.id.buttonBackFromRoutes);
        buttonBack.setOnClickListener(this);

        WalkingViewModel walkingViewModel = new ViewModelProvider(this).get(WalkingViewModel.class);

        walkingViewModel.getDogProfile().observe(this, dogProfile -> {
            if(dogProfile != null) {
                //TODO: Update with distance walked
                textViewHoursWalked.setText("" + df.format(dogProfile.getHoursWalked()) + "hrs");
                textViewDistanceWalked.setText("" + df.format(0) + "km");

            }
            else {
                textViewHoursWalked.setText("0.0");
                textViewDistanceWalked.setText("0.0");
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(polylineReceiver, new IntentFilter("PolylineLocationUpdates"));
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onStart() {
        super.onStart();
        mapView.onStart();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST);
        }
        FusedLocationProviderClient fusedProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
        Utils.getPolylines(this);
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
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonBackFromRoutes:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            updateLocation(location);
        } else {
            //TODO: Handle Errors
            Log.e(Utils.TAG, "Location Not Found");
        }
    }

    private void updateLocation(Location location) {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    private final BroadcastReceiver polylineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            Log.i(Utils.TAG, "Getting Polyline");
            Polyline polyInit = createPolyline();
            List<LatLng> polylinePoints = polyInit.getPoints();
            Map<String, List<Map>> polyline = (Map<String, List<Map>>) intent.getSerializableExtra("Location");
            if(polyline != null) {
                List<Map> polylines = (List<Map>) polyline.get("polyline");
                for(int i = 0; i < polylines.size(); i++) {
                    Map<String, Double> latLng = polylines.get(i);
                    LatLng position = new LatLng(latLng.get("latitude"), latLng.get("longitude"));
                    polylinePoints.add(position);
                }
                updatePolyline(polylinePoints);
            }

        }
    };

    private void updatePolyline(List<LatLng> polyline) {
        Log.i(Utils.TAG, polyline.toString());
        Polyline polylineMap = createPolyline();
        polylineMap.setPoints(polyline);
    }

    private Polyline createPolyline() {
        return mMap.addPolyline(new PolylineOptions()
                .clickable(false));
    }
}