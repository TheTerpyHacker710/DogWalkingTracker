package uk.ac.abertay.cmp309.dogtracker.ui.insurance;

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

public class InsuranceFragment extends Fragment {

    private InsuranceViewModel insuranceViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        insuranceViewModel =
                new ViewModelProvider(this).get(InsuranceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_insurance, container, false);
        final TextView textView = root.findViewById(R.id.text_insurance);
        insuranceViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}