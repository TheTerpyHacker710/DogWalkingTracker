package uk.ac.abertay.cmp309.dogtracker.ui.vets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import uk.ac.abertay.cmp309.dogtracker.R;

public class VetsFragment extends Fragment {

    private VetsViewModel vetsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vetsViewModel =
                new ViewModelProvider(this).get(VetsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_vets, container, false);
        final TextView textView = root.findViewById(R.id.text_vets);
        vetsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}