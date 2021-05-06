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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import uk.ac.abertay.cmp309.dogtracker.ui.walking.WalkingViewModel;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, OnSuccessListener<Location> {

    //Initialise the Location Request flag
    private static final int LOCATION_REQUEST = 1;

    //Declare the Map Objects
    private MapView mapView;
    private GoogleMap mMap;

    //Declare the Views
    private TextView textViewHoursWalked;
    private TextView textViewDistanceWalked;

    //Initialise the DecimalFormat object
    private final DecimalFormat df = new DecimalFormat("0.00");

    //Initialise the MapView Bundle Key
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //OnCreate Method
    //This method is ran when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //Initialising the MapView Bundle
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            //If there is a saved instance state then get bundle from there
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        //Initialise mapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        //Sync Map
        mapView.getMapAsync(this);

        //Initialise the views
        textViewHoursWalked = (TextView) findViewById(R.id.textViewHoursWalkedTodayRoutesValue);
        textViewDistanceWalked = (TextView) findViewById(R.id.textViewDistanceWalkedTodayRoutesValue);
        Button buttonBack = (Button) findViewById(R.id.buttonBackFromRoutes);

        //Start listener on the button
        buttonBack.setOnClickListener(this);

        //Get a model to get the data from the dogProfile
        WalkingViewModel walkingViewModel = new ViewModelProvider(this).get(WalkingViewModel.class);

        //Get the dogProfile and display the data on screen
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

        //Initialise the BroadcastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(polylineReceiver, new IntentFilter("PolylineLocationUpdates"));
    }

    //OnSavedInstanceState method
    //This method is used if there is a saved instance state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //Initialise bundle from saved instance state
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    //OnResume Method
    //This method is ran whenever the map resumes from a  paused state
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    //OnStart Method
    //This method will run when the MapsActivity starts
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        //Check for permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //If there is no permissions, request them
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST);
        }

        //Initialise the Location Provider Client
        FusedLocationProviderClient fusedProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Request last known location and send to listener
        fusedProviderClient.getLastLocation().addOnSuccessListener(this, this);
    }

    //OnStop Method
    //This will run whenever the activity is stopped
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    //OnMapReady Method
    //This method will run when the MapView is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Initialise the map object
        mMap = googleMap;

        //Get the polylines
        Utils.getPolylines(this);
    }

    //OnPause Method
    //This will run whenever the activity goes into a paused state
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    //OnDestroy method
    //This method will run when the activity is destroyed
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    //OnLowMemory Method
    //This method will run when the phone is low on memory
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //OnClick method
    //This method handles the button clicks from the activity
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonBackFromRoutes:
                //If back button is pressed, finish activity
                finish();
                break;
        }
    }

    //OnSuccess Listener
    //This method listens for the activity to receive
    //a location request
    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            //If the location is valid update the map with the location
            updateLocation(location);
        } else {
            //Else, the location was not found
            //TODO: Hanle Errors
            Log.e(Utils.TAG, "Location Not Found");
        }
    }

    //UpdateLocation Method
    //This method will move the camera to a suitable area
    private void updateLocation(Location location) {
        if (mMap != null) {
            //Move the camera
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    //BroadcastReceiver for polyline updates
    private final BroadcastReceiver polylineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Create a new polyline
            Polyline polyInit = createPolyline();

            //Initialise a list of LatLng
            List<LatLng> polylinePoints = polyInit.getPoints();

            //Get the List of LatLng (Map) from the intent
            Map<String, List<Map>> polyline = (Map<String, List<Map>>) intent.getSerializableExtra("Location");

            //Check to see if the polyline is null
            if(polyline != null) {

                //Get the list of LatLng (Map)
                List<Map> polylines = (List<Map>) polyline.get("polyline");

                //Loop through each object in the Map
                for(int i = 0; i < polylines.size(); i++) {

                    //Get the element at "i"
                    Map<String, Double> latLng = polylines.get(i);

                    //Insert the object into a LatLng object
                    LatLng position = new LatLng(latLng.get("latitude"), latLng.get("longitude"));

                    //Add the LatLng to a List of LatLng
                    polylinePoints.add(position);
                }

                //Once all the points are added to List, update the map to add the polyline
                updatePolyline(polylinePoints);
            }

        }
    };

    //UpdatePolyline method
    //This method will add the polyline to the map
    private void updatePolyline(List<LatLng> polyline) {

        //Create a new polyline
        Polyline polylineMap = createPolyline();

        //Set the polyline to the list of LatLng
        polylineMap.setPoints(polyline);

    }

    //CreatePolyline Method
    //This method will return a new polyline object
    private Polyline createPolyline() {

        //Create a new Polyline object and return it
        return mMap.addPolyline(new PolylineOptions()
                .clickable(false));

    }
}