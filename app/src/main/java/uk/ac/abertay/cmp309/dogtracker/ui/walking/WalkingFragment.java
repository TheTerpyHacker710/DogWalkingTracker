package uk.ac.abertay.cmp309.dogtracker.ui.walking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.List;

import uk.ac.abertay.cmp309.dogtracker.MainActivity;
import uk.ac.abertay.cmp309.dogtracker.MapsActivity;
import uk.ac.abertay.cmp309.dogtracker.R;
import uk.ac.abertay.cmp309.dogtracker.RouteActivity;
import uk.ac.abertay.cmp309.dogtracker.Utils;

public class WalkingFragment extends Fragment implements View.OnClickListener {

    private WalkingViewModel walkingViewModel;
    private DecimalFormat df = new DecimalFormat("0.00");
    private static final int MAPS_REQUEST = 2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Walking");

        walkingViewModel = new ViewModelProvider(this).get(WalkingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_walking, container, false);

        final TextView textViewHoursWalked = root.findViewById(R.id.textViewWalkingHoursWalkedValue);
        final TextView textViewHoursWalkedToday = root.findViewById(R.id.textViewWalkingHoursWalkedTodayValue);
        final TextView textViewDistanceWalked = root.findViewById(R.id.textViewWalkingDistanceWalkedValue);
        final TextView textViewDistanceWalkedToday = root.findViewById(R.id.textViewWalkingDistanceWalkedTodayValue);

        Button startWalk = ((Button) root.findViewById(R.id.buttonWalkingStartWalk));
        startWalk.setOnClickListener(this);
        Button viewRoute = ((Button) root.findViewById(R.id.buttonViewRoutes));
        viewRoute.setOnClickListener(this);

        walkingViewModel.getDogProfile().observe(getViewLifecycleOwner(), dogProfile -> {
            if(dogProfile != null) {
                //TODO: Update with distance walked
                textViewHoursWalked.setText("" + df.format(dogProfile.getHoursWalked()) + "hrs");
                textViewHoursWalkedToday.setText("" + df.format(dogProfile.getHoursWalkedToday()) + "hrs");
                textViewDistanceWalked.setText("" + df.format(0) + "km");
                textViewDistanceWalkedToday.setText("" + df.format(0) + "km");

            }
            else {
                textViewHoursWalked.setText("0.0");
                textViewHoursWalkedToday.setText("0.0");
                textViewDistanceWalked.setText("0.0");
                textViewDistanceWalkedToday.setText("0.0");
            }
        });

        return root;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonWalkingStartWalk:
                Toast.makeText(getContext(), "Start Walking", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivityForResult(intent, MAPS_REQUEST);
                break;
            case R.id.buttonViewRoutes:
                //TODO: Open map with all routes ever taken!
                Toast.makeText(getContext(), "Show Routes", Toast.LENGTH_SHORT).show();
                Intent routeIntent = new Intent(getContext(), RouteActivity.class);
                startActivity(routeIntent);
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

                    Utils.updateHoursWalked(data.getIntExtra("minutes", 0), data.getIntExtra("seconds", 0));
                    Log.i(Utils.TAG, "Added Hours Walked");

                    Bundle poly = data.getBundleExtra("polyline");
                    List<LatLng> polyline = (List<LatLng>) poly.getSerializable("polyline");

                    Utils.updatePolyline(polyline);

                }
                break;

        }

    }
}