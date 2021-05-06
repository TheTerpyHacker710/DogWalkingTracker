package uk.ac.abertay.cmp309.dogtracker.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import uk.ac.abertay.cmp309.dogtracker.MainActivity;
import uk.ac.abertay.cmp309.dogtracker.MapsActivity;
import uk.ac.abertay.cmp309.dogtracker.R;
import uk.ac.abertay.cmp309.dogtracker.Utils;
import uk.ac.abertay.cmp309.dogtracker.ui.eating.EatingFragment;
import uk.ac.abertay.cmp309.dogtracker.ui.walking.WalkingFragment;

//This is a controller for the home fragment
//this will control all of the data that is displayed in the home fragment
public class HomeFragment extends Fragment implements View.OnClickListener {

    //Declare the model that will be used
    private HomeViewModel homeViewModel;

    //Declare and initialise variables
    private static final int MAPS_REQUEST = 2;
    private static DecimalFormat df = new DecimalFormat("0.00");

    //On Create -- This will run when the view is created
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Set action bar title to "Home"
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");

        //Initialise model
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Initialise views
        final TextView textViewDogName = root.findViewById(R.id.textViewDogName);
        final TextView textViewHoursWalked = root.findViewById(R.id.textViewHoursWalkedValue);
        final TextView textViewHoursWalkedToday = root.findViewById(R.id.textViewHoursWalkedTodayValue);
        final TextView textViewHoursTrained = root.findViewById(R.id.textViewHoursTrainedValue);
        final TextView textViewHoursTrainedToday = root.findViewById(R.id.textViewHoursTrainedTodayValue);
        final TextView textViewDailyCalories = root.findViewById(R.id.textViewDailyCaloriesValue);

        ImageView imageViewDog = root.findViewById(R.id.imageViewDog);

        Button startWalk = ((Button) root.findViewById(R.id.buttonStartWalk));

        //Start listener for the button
        startWalk.setOnClickListener(this);

        //Retrieve data from model and insert into views
        homeViewModel.getDogProfile().observe(getViewLifecycleOwner(), dogProfile -> {
            if(dogProfile != null) {
                //Fill with dogProfile data
                textViewDogName.setText(dogProfile.getDogName());
                textViewHoursWalked.setText("" + df.format(dogProfile.getHoursWalked()) + "hrs");
                textViewHoursWalkedToday.setText("" + df.format(dogProfile.getHoursWalkedToday()) + "hrs");
                textViewHoursTrained.setText("" + df.format(dogProfile.getHoursTrained()) + "hrs");
                textViewHoursTrainedToday.setText("" + df.format(dogProfile.getHoursTrainedToday()) + "hrs");
                textViewDailyCalories.setText("" + dogProfile.getDailyCalories()  + "kcal");

                Glide.with(this).load(dogProfile.getDogPhotoURL()).into(imageViewDog);
            }
            else {
                //If dogProfile doesn't exist then fill with standard data
                textViewDogName.setText("Not Found");
                textViewHoursWalked.setText("0.0");
                textViewHoursWalkedToday.setText("0.0");
                textViewHoursTrained.setText("0.0");
                textViewHoursTrainedToday.setText("0.0");
                textViewDailyCalories.setText("0.0");
            }
        });

        //Return view
        return root;
    }

    //OnClick function that listens for button click
    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            //If the button matches the startWalk button
            case R.id.buttonStartWalk:

                //Start Maps activity
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intent, MAPS_REQUEST);
                break;
        }
    }

    //OnActivityResult method that listens for the result data from starting the MapActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check the request code
        switch(requestCode) {

            //If request code matches the MapRequest
            case MAPS_REQUEST:

                if(resultCode == Activity.RESULT_OK) {

                    //Run if the resultCode is OK
                    Toast.makeText(getActivity().getApplicationContext(), "Minutes: " + data.getIntExtra("minutes", 0) + " Seconds: " + data.getIntExtra("seconds", 0), Toast.LENGTH_SHORT).show();

                    //TODO: open Walking Fragment

                    //Send hours walked data to be updated on FireStore
                    Utils.updateHoursWalked(data.getIntExtra("minutes", 0), data.getIntExtra("seconds", 0));
                    Log.i(Utils.TAG, "Added Hours Walked");

                    //Send polyline data to be updated on FireStore
                    Bundle poly = data.getBundleExtra("polyline");
                    List<LatLng> polyline = (List<LatLng>) poly.getSerializable("polyline");
                    Utils.updatePolyline(polyline);

                }
                break;

        }

    }

}