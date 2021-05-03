package uk.ac.abertay.cmp309.dogtracker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class LocationExecutableService extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Log.i(Utils.TAG, "Alarm Call Recieved");

        if (isMyServiceRunning(this.context, LocationService.class)) {
            Log.i(Utils.TAG, "already running no need to start again");
        } else {
            Intent background = new Intent(context, LocationService.class);
            context.startService(background);
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
