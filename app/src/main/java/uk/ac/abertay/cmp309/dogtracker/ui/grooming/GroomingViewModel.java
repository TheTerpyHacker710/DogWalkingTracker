package uk.ac.abertay.cmp309.dogtracker.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Model for the grooming fragment
//This will gather all the data needed for the controller/view
public class GroomingViewModel extends ViewModel {

    //Declare data to be sent to the controller
    private MutableLiveData<String> mText;

    public GroomingViewModel() {
        //Initialise and set the data to be sent to the controller
        mText = new MutableLiveData<>();
        mText.setValue("This is grooming fragment");
    }

    //Method to return the data to the controller
    public LiveData<String> getText() {
        return mText;
    }
}