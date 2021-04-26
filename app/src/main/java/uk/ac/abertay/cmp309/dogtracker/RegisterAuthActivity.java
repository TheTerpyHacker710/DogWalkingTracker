package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;
import java.util.Map;

public class RegisterAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            finish();
        }
    }

    public void handleClicks(View view) {
        switch(view.getId()) {
            case R.id.signUpButtonRegister:
                String email = ((EditText) findViewById(R.id.editTextEmailAddressRegister)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPasswordRegister)).getText().toString();
                String rePassword = ((EditText) findViewById(R.id.editTextRePasswordRegister)).getText().toString();
                createAccount(email, password, rePassword);
                break;
            case R.id.signInLink:
                finish();
                break;
        }
    }

    private void createAccount(String email, String password, String rePassword) {

        if(Utils.validateEmail(email) && Utils.validatePasswordRegister(password, rePassword)) {
            Toast.makeText(this, "User Would Be Registered", Toast.LENGTH_SHORT).show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()) {

                            FirebaseUser currentUser = mAuth.getCurrentUser();


                            //Sign in success, update UI with signed in user's info
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

                            db.collection("users").document(currentUser.getUid()).set(dogDetails)
                                    .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                                    .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));

                            Log.d(Utils.TAG, "User Created");

                            finish();
                        }
                    });
        }
        else {
            Toast.makeText(this, "User Would NOT Be Registered", Toast.LENGTH_SHORT).show();
        }
    }
}