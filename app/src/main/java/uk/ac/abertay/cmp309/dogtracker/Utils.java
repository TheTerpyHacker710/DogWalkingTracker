package uk.ac.abertay.cmp309.dogtracker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final String TAG = "dog_tracker_debug";
    private static final String regexEmail = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; //OWASP REGEX FOR EMAIL
    private static final String regexPassword = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{5,128}$"; //ADAPTION OF OWASP REGEX FOR PASSWORDS

    private static DecimalFormat df = new DecimalFormat("0.00");

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser user = mAuth.getCurrentUser();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static boolean validateEmail(String email){
        Pattern pattern = Pattern.compile(regexEmail);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatePasswordRegister(String password, String rePassword){
        if(password.equals(rePassword)) {
            Pattern pattern = Pattern.compile(regexPassword);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        }
        else {
            return false;
        }
    }

    public static boolean validatePassword(String password){
        Pattern pattern = Pattern.compile(regexPassword);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static void updateHoursWalked(int minutesWalked, int secondsWalked){
        double hoursWalked;

        if(secondsWalked > 30){ minutesWalked += 1; }

        hoursWalked = (double)minutesWalked / 60;

        Log.i(Utils.TAG, "" + Double.parseDouble(df.format(hoursWalked)));

        db.collection("users").document(user.getUid()).update("hoursWalked", FieldValue.increment(Double.parseDouble(df.format(hoursWalked))), "hoursWalkedToday", FieldValue.increment(Double.parseDouble(df.format(hoursWalked))))
                .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));
    }

}
