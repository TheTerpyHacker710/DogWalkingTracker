package uk.ac.abertay.cmp309.dogtracker.ui.walking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.abertay.cmp309.dogtracker.MainActivity;
import uk.ac.abertay.cmp309.dogtracker.R;

public class WalkingFragment extends Fragment {

    private WalkingViewModel walkingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Walking");

        walkingViewModel = new ViewModelProvider(this).get(WalkingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_walking, container, false);

        return root;
    }
}