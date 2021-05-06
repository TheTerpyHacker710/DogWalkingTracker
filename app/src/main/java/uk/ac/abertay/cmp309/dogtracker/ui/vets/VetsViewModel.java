package uk.ac.abertay.cmp309.dogtracker.ui.vets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Model for the vets fragment
//This will gather all the data needed for the controller/view
public class VetsViewModel extends ViewModel {

    //Declare data to be sent to the controller
    private MutableLiveData<String> mText;

    //Initialise and set the data to be sent to the controller
    public VetsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is vets fragment");
    }

    //Method to return the data to the controller
    public LiveData<String> getText() {
        return mText;
    }
}