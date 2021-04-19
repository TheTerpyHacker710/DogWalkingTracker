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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.context.AttributeContext;

public class RegisterAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);

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
            case R.id.signUpButtonRegister:
                String email = ((EditText) findViewById(R.id.editTextEmailAddressRegister)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPasswordRegister)).getText().toString();
                String rePassword = ((EditText) findViewById(R.id.editTextRePasswordRegister)).getText().toString();
                createAccount(email, password, rePassword);
                break;
            case R.id.signInLink:
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void createAccount(String email, String password, String rePassword) {

        //TODO: Register User
        if(Utils.validateEmail(email) && Utils.validatePasswordRegister(password, rePassword)) {
            Toast.makeText(this, "User Would Be Registered", Toast.LENGTH_SHORT).show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //Sign in success, update UI with signed in user's info
                                Log.d(Utils.TAG, "User Created");
                                finish();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "User Would NOT Be Registered", Toast.LENGTH_SHORT).show();
        }
    }
}