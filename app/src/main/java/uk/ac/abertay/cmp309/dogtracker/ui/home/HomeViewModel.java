package uk.ac.abertay.cmp309.dogtracker.ui.home;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private String hoursWalked;
    private String hoursWalkedToday;
    private String hoursTrained;
    private String hoursTrainedToday;
    private String dailyCalories;
    private String dogName;
    private Uri dogPhotoURL;

    public HomeViewModel() {

    }

    public String getHoursWalked(){
        return hoursWalked;
    }

    public String getHoursWalkedToday(){
        return hoursWalkedToday;
    }

    public String getHoursTrained(){
        return hoursTrained;
    }

    public String getHoursTrainedToday(){
        return hoursTrainedToday;
    }

    public String getDailyCalories(){
        return dailyCalories;
    }

    public String getDogName() {
        return dogName;
    }

    public Uri getDogPhotoURL(){
        return dogPhotoURL;
    }
}