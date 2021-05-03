package uk.ac.abertay.cmp309.dogtracker.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

import uk.ac.abertay.cmp309.dogtracker.MainActivity;
import uk.ac.abertay.cmp309.dogtracker.MapsActivity;
import uk.ac.abertay.cmp309.dogtracker.R;
import uk.ac.abertay.cmp309.dogtracker.Utils;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private static final int MAPS_REQUEST = 2;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textViewDogName = root.findViewById(R.id.textViewDogName);
        final TextView textViewHoursWalked = root.findViewById(R.id.textViewHoursWalkedValue);
        final TextView textViewHoursWalkedToday = root.findViewById(R.id.textViewHoursWalkedTodayValue);
        final TextView textViewHoursTrained = root.findViewById(R.id.textViewHoursTrainedValue);
        final TextView textViewHoursTrainedToday = root.findViewById(R.id.textViewHoursTrainedTodayValue);
        final TextView textViewDailyCalories = root.findViewById(R.id.textViewDailyCaloriesValue);

        ImageView imageViewDog = root.findViewById(R.id.imageViewDog);

        Button startWalk = ((Button) root.findViewById(R.id.buttonStartWalk));
        startWalk.setOnClickListener(this);

        homeViewModel.getDogProfile().observe(getViewLifecycleOwner(), dogProfile -> {
            if(dogProfile != null) {
                textViewDogName.setText(dogProfile.getDogName());
                textViewHoursWalked.setText("" + df.format(dogProfile.getHoursWalked()) + "hrs");
                textViewHoursWalkedToday.setText("" + df.format(dogProfile.getHoursWalkedToday()) + "hrs");
                textViewHoursTrained.setText("" + df.format(dogProfile.getHoursTrained()) + "hrs");
                textViewHoursTrainedToday.setText("" + df.format(dogProfile.getHoursTrainedToday()) + "hrs");
                textViewDailyCalories.setText("" + dogProfile.getDailyCalories()  + "kcal");

                Glide.with(this).load(dogProfile.getDogPhotoURL()).into(imageViewDog);
            }
            else {
                textViewDogName.setText("Not Found");
                textViewHoursWalked.setText("0.0");
                textViewHoursWalkedToday.setText("0.0");
                textViewHoursTrained.setText("0.0");
                textViewHoursTrainedToday.setText("0.0");
                textViewDailyCalories.setText("0.0");
            }
        });
        return root;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonStartWalk:
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intent, MAPS_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

            case MAPS_REQUEST:

                if(resultCode == Activity.RESULT_OK) {

                    Toast.makeText(getActivity().getApplicationContext(), "Minutes: " + data.getIntExtra("minutes", 0) + " Seconds: " + data.getIntExtra("seconds", 0), Toast.LENGTH_SHORT).show();

                    //TODO: Send data to be updated on Firestore and open Walking Fragment AND STORE POLYLINE

                    Utils.updateHoursWalked(data.getIntExtra("minutes", 0), data.getIntExtra("seconds", 0));
                }

                break;

        }

    }

}