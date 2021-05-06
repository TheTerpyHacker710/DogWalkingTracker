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

//This is a controller for the insurance fragment
//this will control all of the data that is displayed in the insurance fragment
public class InsuranceFragment extends Fragment {

    //Declare the model that will be used
    private InsuranceViewModel insuranceViewModel;

    //On Create -- This will run when the view is created
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Initialise the model
        insuranceViewModel = new ViewModelProvider(this).get(InsuranceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_insurance, container, false);

        //Get the text view on the fragment
        final TextView textView = root.findViewById(R.id.text_insurance);

        //Retrieve text from model and insert into textView
        insuranceViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        //Return the view
        return root;
    }
}