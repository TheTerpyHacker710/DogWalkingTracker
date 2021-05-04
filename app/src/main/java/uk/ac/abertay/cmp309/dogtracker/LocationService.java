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



public class LocationService extends Service {

    private static final String CHANNEL_DEFAULT = "Default Importance";
    private static final String CHANNEL_ID = "Maps Notification";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Utils.TAG, "Creating Service");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(Utils.TAG, "No Permissions");
            return;
        }
        Log.i(Utils.TAG, "Getting Provider Client");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = getSystemService(NotificationManager.class);
        CharSequence name = "Maps Notification";
        String description = "A channel for maps notification";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Dog Tracker")
                .setContentText("This app is tracking your location")
                .setSmallIcon(R.drawable.ic_home)
                .setContentIntent(pendingIntent)
                .setTicker("1")
                .build();

        startForeground(3, notification);

        Log.d(Utils.TAG, "service Starting");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10);
        locationRequest.setInterval(5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(Utils.TAG, "Sending Results");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null) {
                    Log.d(Utils.TAG, locationResult.getLastLocation().toString());
                    try{
                        sendLocationToActivity(locationResult.getLastLocation(), "Found");
                    }
                    catch(Exception e) {
                        Toast.makeText(LocationService.this, "Found Location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        boolean checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(fusedLocationProviderClient != null && checkResult) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(Utils.TAG, "Stopping Service");
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void sendLocationToActivity(Location location, String message) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("Status", message);
        Bundle b = new Bundle();
        b.putParcelable("Location", location);
        intent.putExtra("Location", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}