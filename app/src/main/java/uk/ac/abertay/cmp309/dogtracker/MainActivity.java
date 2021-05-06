package uk.ac.abertay.cmp309.dogtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//MainActivity
//Where the program starts
public class MainActivity extends AppCompatActivity {

    //Declare Variables
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Boolean profileSet;

    //OnCreate Method
    //Method is called when the MainActivity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialising the navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_walk, R.id.nav_eat, R.id.nav_train, R.id.nav_vet, R.id.nav_health, R.id.nav_groom, R.id.nav_insurance)
                .setDrawerLayout(drawer)
                .build();

        //Building the NavController and UI
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Initialising the Firebase variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Calling the alarm manager to reset variables
        AlarmHandler alarmHandler = new AlarmHandler(this);
        alarmHandler.cancelAlarmManager();
        alarmHandler.setAlarmManager();
    }

    //OnStart method
    //This is called whenever the MainActivity is called
    @Override
    protected void onStart() {
        super.onStart();

        //If the user is not already signed in, then take to sign in page, else, continue
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            //Start AuthActivity
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        }
        if(currentUser != null) {
            //If user is logged in then update the UI
            updateUI(currentUser);
        }
    }

    //OnBackPressed method
    //This method overrides the native function of the
    //back button on the phone, instead it will close the
    //navigation drawer
    @Override
    public void onBackPressed() {
        //Check to see if the drawer is open
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            //If it is the close it
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Else, use the native functionality
            super.onBackPressed();
        }
    }

    //OnCreateOptionsMenu method
    //When the options menu is created run this
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    //OnSupportNavigateUp method
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //OnOptionsItemSelected method
    //This method overrides the native functionality of the toolbar options
    //It will check to see which item was pressed and perform some functionality
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Check which item was pressed
        if(item.getItemId() == R.id.action_signout) {

            //Display a message that the user is signed out and sign the user out
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();

            mAuth.signOut();

            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    //UpdateUI method
    //This method will check to see if the user has entered their profile information
    //If the profile is not set then send them to the ProfileActivity
    private void updateUI(FirebaseUser user) {

        //Get a reference from the database
        DocumentReference docRef = db.collection("users").document(user.getUid());

        //Get the document from the reference
        docRef.get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                //Get the results
                DocumentSnapshot document = task.getResult();

                if(document.exists()) {

                    //Get the profileSet variable
                    profileSet = ((Boolean) document.get("profileSet"));

                    try {

                        //If the profile is not set then send the user to the ProfileActivity
                        if (!profileSet) {

                            //Create new intent to send user to the ProfileActivity
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                    catch (Exception e) {

                        //Catch any errors and print the stack trace
                        e.printStackTrace();
                    }
                }
                else {
                    //TODO: Hanlde Errors
                    //If the document doesn't exist then display error in log
                    Log.e(Utils.TAG, "Error -- Document does NOT exist");
                }
            }
            else {
                //TODO: Handle Errors
                //Task Failed, print error in log
                Log.e(Utils.TAG, "Task failed with exception: ", task.getException());
            }
        });
    }

}
