package uk.ac.abertay.cmp309.dogtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class LocationAlarmHandler {
    private Context context;

    public LocationAlarmHandler(Context context) {
        this.context = context;
    }

    public void setAlarmManager() {
        Intent intent = new Intent(context, LocationExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 3, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 5000 * 12, sender);
        }
    }

    public void cancelAlarmManager() {
        Intent intent = new Intent(context, LocationExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 3, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(sender);
        }
    }
}
