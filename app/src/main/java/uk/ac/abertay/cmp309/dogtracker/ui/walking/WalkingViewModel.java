package uk.ac.abertay.cmp309.dogtracker.ui.walking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WalkingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WalkingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is walking fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}