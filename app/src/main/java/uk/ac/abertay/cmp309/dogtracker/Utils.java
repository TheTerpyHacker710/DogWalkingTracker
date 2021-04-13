package uk.ac.abertay.cmp309.dogtracker;

import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final String TAG = "dog_tracker_debug";
    private static final String regexEmail = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; //OWASP REGEX FOR EMAIL
    private static final String regexPassword = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{5,128}$"; //ADAPTION OF OWASP REGEX FOR PASSWORDS

    public static boolean validateEmail(String email){
        Pattern pattern = Pattern.compile(regexEmail);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatePassword(String password, String rePassword){
        if(password.equals(rePassword)) {
            Pattern pattern = Pattern.compile(regexPassword);
            Matcher matcher = pattern.matcher(password);
            Log.i(TAG, "MATCHES and Meets criteria");
            return matcher.matches();
        }
        else {
            return false;
        }
    }
}
