package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
            Intent resultIntent = new Intent();
            resultIntent.putExtra("user", currentUser);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    public void handleClicks(View view) {
        switch(view.getId()) {
            case R.id.signInButton:
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.signUpLink:
                Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, RegisterAuthActivity.class);
                startActivity(intent);
                break;
        }
    }
}