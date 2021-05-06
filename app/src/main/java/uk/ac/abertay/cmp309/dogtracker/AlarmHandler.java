package uk.ac.abertay.cmp309.dogtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

//Class that initiates an alarm to go off everynight at 23:59
//This will reset the values in the database for the daily values
public class AlarmHandler {

    //Declare the context
    private Context context;

    //Constructor that initialises the context
    public AlarmHandler(Context context) {
        this.context = context;
    }

    //SetAlarmManager method
    //This method will set up the alarm manager
    public void setAlarmManager() {

        //Initialise a new intent and pending intent to start the service
        Intent intent = new Intent(context, ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, 0);

        //Initialise the AlarmManager class
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Check to see if the alarm manager has been initialised
        if(am != null) {

            //If initialised then set the time to 23:59
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);

            //Set the alarm manager to run everynight at 23:59
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
        }
    }

    //CancelAlarm Method
    //This method cancels the currently running alarm
    public void cancelAlarmManager() {

        //Initialise the intent and pending intent to cancel the service
        Intent intent = new Intent(context, ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, 0);

        //Initialise the alarm manager class
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Check to see if the alarm manager is initialise successfully
        if(am != null) {

            //Cancel the alarm
            am.cancel(sender);
        }
    }
}
