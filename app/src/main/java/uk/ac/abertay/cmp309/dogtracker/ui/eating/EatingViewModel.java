package uk.ac.abertay.cmp309.dogtracker.ui.eating;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EatingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EatingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is eating fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}