package uk.ac.abertay.cmp309.dogtracker;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


//Location Service
//Retrieves user's location, in foreground service
public class LocationService extends Service {

    //Initialise notification variables
    private static final String CHANNEL_DEFAULT = "Default Importance";
    private static final String CHANNEL_ID = "Maps Notification";

    //Declare Location variables
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    //Declare NotificationManager variable
    private NotificationManager notificationManager;

    //OnCreate Method
    //This will run when the activity is first created
    @Override
    public void onCreate() {
        super.onCreate();

        //Check to see if the user has granted permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //If the user has not granted permissions then return
            Log.i(Utils.TAG, "No Permissions");
            return;
        }

        //Initiate LocationClient variables and Initiate the Notification Manager variables
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = getSystemService(NotificationManager.class);

        //Set notification Variables
        CharSequence name = "Maps Notification";
        String description = "A channel for maps notification";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        //Set Notification Channel Variables and create Channel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    //OnStartCommand
    //This will run when startService is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Set intents and PendingIntent for notification
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Build Notification
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Dog Tracker")
                .setContentText("This app is tracking your location")
                .setSmallIcon(R.drawable.ic_home)
                .setContentIntent(pendingIntent)
                .setTicker("1")
                .build();

        //Start the service in the foreground
        startForeground(3, notification);

        //Build the LocationRequest object
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10);
        locationRequest.setInterval(5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Build the location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                //Check if the location is valid
                if (locationResult != null) {
                    try{
                        //Try to send the location data back to the activity
                        sendLocationToActivity(locationResult.getLastLocation(), "Found");
                    }
                    catch(Exception e) {
                        //Catch any errors and display them on screen
                        Log.i(Utils.TAG, "Error: ", e);
                    }
                }
            }
        };

        //Check the permissions are granted
        boolean checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(fusedLocationProviderClient != null && checkResult) {
            //Request the location from the location callback
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }

        //Return and start again
        return START_STICKY;
    }

    //OnDestroy method
    //Method is called when stopService() is called
    @Override
    public void onDestroy() {

        //Check to see if location provider and location callback are valid
        if (fusedLocationProviderClient != null && locationCallback != null) {
            //Remove the callbacks
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

        //Stop the foreground process
        stopForeground(true);

        //Destroy the service
        super.onDestroy();
    }

    //OnBind method
    //Method is called when bindService() is called
    @Override
    public IBinder onBind(Intent arg0) {

        //Return null as onBind is never used
        return null;
    }

    //SendLocationToActivity method
    //Method is used to send data back to the MapsActivity to update the UI
    private void sendLocationToActivity(Location location, String message) {

        //Create new intent with filter to send location back to BroadcastReceiver
        Intent intent = new Intent("GPSLocationUpdates");

        //Add extras to intent
        intent.putExtra("Status", message);
        Bundle b = new Bundle();
        b.putParcelable("Location", location);
        intent.putExtra("Location", b);

        //Broadcast intent to BroadcastReceiver
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}