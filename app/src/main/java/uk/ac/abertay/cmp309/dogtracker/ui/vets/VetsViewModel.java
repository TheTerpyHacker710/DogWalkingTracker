package uk.ac.abertay.cmp309.dogtracker.ui.vets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VetsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VetsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is vets fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}