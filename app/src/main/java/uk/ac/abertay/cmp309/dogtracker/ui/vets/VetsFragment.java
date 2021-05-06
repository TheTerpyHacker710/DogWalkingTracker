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

//This is a controller for the vets fragment
//this will control all of the data that is displayed in the vets fragment
public class VetsFragment extends Fragment {

    //Declare the model that will be used
    private VetsViewModel vetsViewModel;

    //On Create -- This will run when the view is created
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Initialise the model
        vetsViewModel = new ViewModelProvider(this).get(VetsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_vets, container, false);

        //Get the text view on the fragment
        final TextView textView = root.findViewById(R.id.text_vets);

        //Retrieve text from model and insert into textView
        vetsViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        //Return the view
        return root;
    }
}