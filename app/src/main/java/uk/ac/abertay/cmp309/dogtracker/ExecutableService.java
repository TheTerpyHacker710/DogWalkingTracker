package uk.ac.abertay.cmp309.dogtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class ExecutableService extends BroadcastReceiver {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: MAKE SURE THIS WORKS
        db.collection("users").document(user.getUid()).update("hoursWalkedToday", Double.parseDouble(df.format(0)), "hoursTrainedToday", Double.parseDouble(df.format(0)), "dailyCalories", 0)
                .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Updated!"))
                .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));
    }
}
