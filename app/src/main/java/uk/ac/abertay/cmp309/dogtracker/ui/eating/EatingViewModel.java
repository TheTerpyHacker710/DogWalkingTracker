package uk.ac.abertay.cmp309.dogtracker.ui.eating;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Model for the eating fragment
//This will gather all the data needed for the controller/view
public class EatingViewModel extends ViewModel {

    //Declare data to be sent to the controller
    private MutableLiveData<String> mText;

    public EatingViewModel() {
        //Initialise and set the data to be sent to the controller
        mText = new MutableLiveData<>();
        mText.setValue("This is eating fragment");
    }

    //Method to return the data to the controller
    public LiveData<String> getText() {
        return mText;
    }
}