package uk.ac.abertay.cmp309.dogtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ExecutableService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: RESET HOURSWALKEDTODAY EVERY NIGHT AT 1AM
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}
