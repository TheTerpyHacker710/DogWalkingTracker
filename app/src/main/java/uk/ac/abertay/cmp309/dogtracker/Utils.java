package uk.ac.abertay.cmp309.dogtracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //Initialise Debug Tag
    public static final String TAG = "dog_tracker_debug";

    //Initialise the RegEx
    private static final String regexEmail = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; //OWASP REGEX FOR EMAIL
    private static final String regexPassword = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{5,128}$"; //ADAPTION OF OWASP REGEX FOR PASSWORDS

    //Initialise the DecimalFormat object
    private static DecimalFormat df = new DecimalFormat("0.00");

    //Initialise the Firebase Objects
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser user = mAuth.getCurrentUser();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();


    //ValidateEmail method
    //This method will validate the email
    public static boolean validateEmail(String email){
        //Validate the email
        Pattern pattern = Pattern.compile(regexEmail);
        Matcher matcher = pattern.matcher(email);

        //return the value
        return matcher.matches();
    }

    //ValidatePasswordRegister method
    //this will validate the password from the register activtiy
    public static boolean validatePasswordRegister(String password, String rePassword){

        //Check if both passwords match
        if(password.equals(rePassword)) {

            //Validate the password
            Pattern pattern = Pattern.compile(regexPassword);
            Matcher matcher = pattern.matcher(password);

            //return value
            return matcher.matches();
        }
        else {
            //return that the passwords don't match
            return false;
        }
    }

    //ValidatePassword method
    //this method will validate the password
    public static boolean validatePassword(String password){
        //Validate the password
        Pattern pattern = Pattern.compile(regexPassword);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    //UpdateHoursWalked method
    //This method will update the hours walked in the database
    public static void updateHoursWalked(int minutesWalked, int secondsWalked){

        //Declare Variables
        double hoursWalked;

        //Check if seconds is after 30 and round up
        if(secondsWalked > 30){ minutesWalked += 1; }

        //Calculate the hours walked
        hoursWalked = (double)minutesWalked / 60;

        //Update the database with the data
        db.collection("users").document(user.getUid()).update("hoursWalked", FieldValue.increment(Double.parseDouble(df.format(hoursWalked))), "hoursWalkedToday", FieldValue.increment(Double.parseDouble(df.format(hoursWalked))))
                .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));
    }

    //UpdatePolyline method
    //this method will send the polyline data to the database to be stored
    public static void updatePolyline(List<LatLng> polyline) {

        //Declare the Map and enter the polyline
        Map<String, List<LatLng>> dogDetails = new HashMap<>();
        dogDetails.put("polyline", polyline);

        //Send data to database
        db.collection("users").document(user.getUid()).collection("polylines").document().set(dogDetails)
                .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));
    }

    //GetPolylines method
    //This method will send the polylines to the routes activity to be displayed on the map
    public static void getPolylines(Context context) {

        //Get a reference to the collection of polylines
        CollectionReference colRef = db.collection("users").document(user.getUid()).collection("polylines");
        colRef.get().addOnCompleteListener(task -> {

            if(task.isSuccessful()) {

                for(QueryDocumentSnapshot document : task.getResult()) {

                    //Send the polylines to the activity
                    sendPolylineToActivity(document.getData(), context);

                }

            }
            else {
                //TODO: HANDLE ERRORS
                Log.e(TAG, "Error getting documents: ", task.getException());

            }

        });

    }

    //SendPolylineToActivity method
    //This method will broadcast the polylines to the activity to be displayed on the map
    private static void sendPolylineToActivity(Map<String, Object> location, Context context) {

        //Create a new intent to broadcast the polylines
        Intent intent = new Intent("PolylineLocationUpdates");

        //Put the data into the intent
        intent.putExtra("Location", (Serializable) location);

        //Broadcast the data to the activity
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
