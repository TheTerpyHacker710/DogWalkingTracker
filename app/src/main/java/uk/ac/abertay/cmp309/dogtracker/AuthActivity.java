package uk.ac.abertay.cmp309.dogtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Authentication Activity to allow user to login
public class AuthActivity extends AppCompatActivity {

    //Declare FirebaseAuth variable
    private FirebaseAuth mAuth;

    //OnCreate method that will be ran when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //Initialise the FirebaseAuth variable
        mAuth = FirebaseAuth.getInstance();
    }

    //OnStart method that runs when the activity starts
    @Override
    protected void onStart() {
        super.onStart();

        //Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //If user is logged in then return to MainActivity
            finish();
        }
    }

    //HandleClicks Method
    //This method handles the click events on the buttons/links
    public void handleClicks(View view) {

        //Check the buttons ID
        switch(view.getId()) {

            //If the ID matches the sign in button
            case R.id.signInButton:

                //Retrieve user input data from form
                String email = ((EditText) findViewById(R.id.editTextEmailAddress)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

                //Sign in with the inputted data
                signIn(email, password);

                break;

            //If the ID matches the register link
            case R.id.signUpLink:

                //Start the register activity
                Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, RegisterAuthActivity.class);
                startActivity(intent);

                break;
        }
    }

    //SignIn method
    //Called when the user wants to sign in
    private void signIn(String email, String password) {

        //Validate the email and password
        //If both meet the validation criteria then sign in
        if(Utils.validateEmail(email) && Utils.validatePassword(password)) {
            Toast.makeText(this, "Credentials validated", Toast.LENGTH_SHORT).show();

            //SignIn with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {

                        //If log in is successful return to MainActivity
                        if(task.isSuccessful()) {

                            //sign in success, update UI
                            finish();

                        }
                        else {

                            //Sign in failed, display error message
                            Log.e(Utils.TAG, "Sign In Failed");
                            Toast.makeText(AuthActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}