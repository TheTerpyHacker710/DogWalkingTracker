package uk.ac.abertay.cmp309.dogtracker.ui.eating;

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

public class EatingFragment extends Fragment {

    private EatingViewModel eatingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eatingViewModel =
                new ViewModelProvider(this).get(EatingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eating, container, false);
        final TextView textView = root.findViewById(R.id.text_eating);
        eatingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}