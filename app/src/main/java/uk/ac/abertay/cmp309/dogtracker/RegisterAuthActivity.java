package uk.ac.abertay.cmp309.dogtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterAuthActivity extends AppCompatActivity {

    //Initialise the Firebase Objects
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //OnCreate Method
    //This method is ran when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);

        //Initialise the variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    //OnStart Method
    //This is ran whenever the activity is started
    @Override
    protected void onStart() {
        super.onStart();
        //Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //If the user is logged in then return to previous activity
            finish();
        }
    }

    //HandleClicks Method
    //This method will handle the clicks of the buttons
    public void handleClicks(View view) {
        switch(view.getId()) {

            //If it was the register button
            case R.id.signUpButtonRegister:

                //Get the user input
                String email = ((EditText) findViewById(R.id.editTextEmailAddressRegister)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPasswordRegister)).getText().toString();
                String rePassword = ((EditText) findViewById(R.id.editTextRePasswordRegister)).getText().toString();

                //Create the account
                createAccount(email, password, rePassword);

                break;

            //If it was the sign in link
            case R.id.signInLink:

                //return to the previous activity
                finish();

                break;

        }
    }

    //CreateAccount Method
    //This method will validate the user input and create the account
    private void createAccount(String email, String password, String rePassword) {

        //Valid the user input
        if(Utils.validateEmail(email) && Utils.validatePasswordRegister(password, rePassword)) {

            //If the input is valid then create the account with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()) {

                            //If the registration was  successful then get the current user
                            FirebaseUser currentUser = mAuth.getCurrentUser();


                            //Sign in success, update database with the fresh information
                            Map<String, Object> dogDetails = new HashMap<>();
                            dogDetails.put("hoursWalked", 0.0);
                            dogDetails.put("hoursWalkedToday", 0.0);
                            dogDetails.put("hoursTrained", 0.0);
                            dogDetails.put("hoursTrainedToday", 0.0);
                            dogDetails.put("dailyCalories", 0);
                            dogDetails.put("caloriesPerMeal", 0);
                            dogDetails.put("dogName", "");
                            dogDetails.put("dogAge", 0);
                            dogDetails.put("dogPhotoURL", "");
                            dogDetails.put("profileSet", false);

                            //Update Database
                            db.collection("users").document(currentUser.getUid()).set(dogDetails)
                                    .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                                    .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));

                            //Return to previous activity
                            finish();
                        }
                    });
        }
        else {
            //Display error message
            Toast.makeText(this, "User NOT Registered", Toast.LENGTH_SHORT).show();
        }
    }
}