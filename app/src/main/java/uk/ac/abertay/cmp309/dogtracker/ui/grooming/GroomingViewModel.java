package uk.ac.abertay.cmp309.dogtracker.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroomingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GroomingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is grooming fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}