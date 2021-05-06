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

    //Declare Map Variables
    private static final int LOCATION_REQUEST = 1;
    private MapView mapView;
    private GoogleMap mMap;

    //Declare Views
    private TextView textViewTimer;
    private Button startButton;

    //Declare Marker and Polyline Variables
    private Marker markerMyLocation;
    private List<LatLng> points;
    private Polyline polyline;

    //Declare timing variables
    private boolean recordWalk = false;
    private long startTime = 0;
    private int seconds = 0;
    private int minutes = 0;

    //Initialise new handler to run timer
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            //When timer is running calculate time
            long millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            //Print time to timer view
            textViewTimer.setText(String.format("%d:%02d", minutes, seconds));

            //Delay for .5s
            timerHandler.postDelayed(this, 500);
        }
    };


    //Initialise Bundle Key for MapView
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //OnCreate Method
    //This method is ran when the MapsActivity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        //Initialise Views
        textViewTimer = (TextView) findViewById(R.id.textViewTimeWalked);
        startButton = (Button) findViewById(R.id.buttonStartWalking);

        //Initialise BroadcastReciever
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter("GPSLocationUpdates"));

        //Create new Intent and start a foreground service to get users location
        Intent intent = new Intent(this, LocationService.class);
        startForegroundService(intent);
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

    //OnBackPressed method
    //Overrides the native functionality of the back button
    //This will cancle the walk
    @Override
    public void onBackPressed() {
        cancelWalk();
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

        //Initialise the Map variable
        mMap = googleMap;

        //Initialise the polyline that tracks the user
        polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(false));

        //Initialise the List of LatLng that tracks user
        points = polyline.getPoints();

        //Initialise the marker of the user's location
        markerMyLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0))
                .title("My Position")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
        );
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

    //OnSuccess Listener
    //This method listens for the activity to receive
    //a location request
    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            //If the location is valid update the map with the location
            updateMap(location);
        } else {
            //Else, the location was not found
            //TODO: Hanle Errors
            Log.e(Utils.TAG, "Location Not Found");
        }
    }

    //HandleClicks method
    //This method handles the button clicks from the activity
    @SuppressLint("NonConstantResourceId")
    public void handleClicks(View view) {

        //Check which button was clicked
        switch (view.getId()) {

            //If it was the Start/Cancel walking button
            case R.id.buttonStartWalking:

                //Check if the button was Cancel or Start
                if (startButton.getText().equals("Cancel!")) {

                    //If it was cancel the cancel the walk
                    cancelWalk();

                } else {

                    //Else, start the walk
                    startWalk();

                }

                break;

                //If it was the finish walking button
                case R.id.buttonFinishWalking:

                    //Finish the walk
                    finishWalk();

                    break;

        }

    }

    //CancelWalk Method
    //This method will cancel the walk and discard the results
    //TODO: Unregister receiver
    private void cancelWalk() {
        //TODO: ADD POP UP TO ASK USER TO CONFIRM

        //Stop the timer
        timerHandler.removeCallbacks(timerRunnable);

        //Create an intent to stop the location service
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);

        //Return to the previous activity
        finish();
    }

    //StartWalk Method
    //This method will start the timer and start recording the path taken
    private void startWalk() {

        //Display message to user that they are now walking
        Toast.makeText(this, "Start Walking", Toast.LENGTH_SHORT).show();

        //Get the start time and start the timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        //Change the buttons text to cancel
        startButton.setText("Cancel!");

        //Set the recordWalk Variable to true
        recordWalk = true;
    }

    //FinishWalk Method
    //This method is used to finish the walk and update the database
    private void finishWalk() {

        //Check to make sure the timer has ran
        if (minutes > 0 || seconds > 0) {

            //Show a message to the user that the walk has finished
            Toast.makeText(this, "Finished Walked", Toast.LENGTH_SHORT).show();

            //Stop the timer
            timerHandler.removeCallbacks(timerRunnable);

            //Change the button text back to start
            startButton.setText("Start!");

            //Create a new intent to stop the location service
            Intent intent = new Intent(this, LocationService.class);
            stopService(intent);

            //Create a new result intent to send data back to activity to be processed
            Intent resultIntent = new Intent();

            //Put time data into intent to be processed
            resultIntent.putExtra("minutes", minutes);
            resultIntent.putExtra("seconds", seconds);

            //Bundle the polyline to be sent to database
            Bundle poly = new Bundle();
            poly.putSerializable("polyline", (Serializable) points);

            //Put polyline bundle into intent to be processed
            resultIntent.putExtra("polyline", poly);

            //Set the activity result
            setResult(Activity.RESULT_OK, resultIntent);

            //Finish the activity and send data back to previous activity
            finish();

        } else {

            //Display a message to the user that they need to start the walk to finish it
            Toast.makeText(this, "Please start the walk!", Toast.LENGTH_SHORT).show();

        }
    }

    //UpdateMap Method
    //This method is used to update the location of the user
    //and to track their movements with a polyline
    public void updateMap(Location location) {

        //Check to make sure the map object is initialised
        if (mMap != null) {

            //If the walk has started record the walk
            if (recordWalk) {

                //Add current location to the list of LatLng objects
                points.add(new LatLng(location.getLatitude(), location.getLongitude()));

                //Set the polyline to the list of LatLng objects
                polyline.setPoints(points);

            }

            //Update the marker location to the new location
            markerMyLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    //BroadcastReceiver for Location updates
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        //OnReceive method
        //This method will receive the location updates and update the map
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the bundle of location updates
            Bundle b = intent.getBundleExtra("Location");

            //Convert to location
            Location lastKnownLoc = (Location) b.getParcelable("Location");

            //Check to make sure the location is valid
            if (lastKnownLoc != null) {

                //If location is valid the update the map
                updateMap(lastKnownLoc);

            }

        }
    };
}