package uk.ac.abertay.cmp309.dogtracker.ui.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import uk.ac.abertay.cmp309.dogtracker.DogProfile;
import uk.ac.abertay.cmp309.dogtracker.R;
import uk.ac.abertay.cmp309.dogtracker.ui.eating.EatingViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textViewDogName = root.findViewById(R.id.textViewDogName);
        final TextView textViewHoursWalked = root.findViewById(R.id.textViewHoursWalkedValue);
        final TextView textViewHoursWalkedToday = root.findViewById(R.id.textViewHoursWalkedTodayValue);
        final TextView textViewHoursTrained = root.findViewById(R.id.textViewHoursTrainedValue);
        final TextView textViewHoursTrainedToday = root.findViewById(R.id.textViewHoursTrainedTodayValue);
        final TextView textViewDailyCalories = root.findViewById(R.id.textViewDailyCaloriesValue);

        ImageView imageViewDog = root.findViewById(R.id.imageViewDog);



        homeViewModel.getDogProfile().observe(getViewLifecycleOwner(), dogProfile -> {
            if(dogProfile != null) {
                textViewDogName.setText(dogProfile.getDogName());
                textViewHoursWalked.setText(Integer.toString(dogProfile.getHoursWalked()) + "hrs");
                textViewHoursWalkedToday.setText(Integer.toString(dogProfile.getHoursWalkedToday()) + "hrs");
                textViewHoursTrained.setText(Integer.toString(dogProfile.getHoursTrained()) + "hrs");
                textViewHoursTrainedToday.setText(Integer.toString(dogProfile.getHoursTrainedToday()) + "hrs");
                textViewDailyCalories.setText(Integer.toString(dogProfile.getDailyCalories())  + "kcal");

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
}