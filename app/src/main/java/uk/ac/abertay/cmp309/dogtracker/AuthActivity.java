package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();
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
            case R.id.signInButton:
                String email = ((EditText) findViewById(R.id.editTextEmailAddress)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                signIn(email, password);
                break;
            case R.id.signUpLink:
                Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, RegisterAuthActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void signIn(String email, String password) {
        if(Utils.validateEmail(email) && Utils.validatePassword(password)) {
            Toast.makeText(this, "Credentials validated", Toast.LENGTH_SHORT).show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()) {
                            //sign in success, update UI
                            Log.d(Utils.TAG, "signInWithEmail:success");
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