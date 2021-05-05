package uk.ac.abertay.cmp309.dogtracker.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import uk.ac.abertay.cmp309.dogtracker.DogProfile;
import uk.ac.abertay.cmp309.dogtracker.Utils;

public class HomeViewModel extends ViewModel {


    public HomeViewModel() {

    }

    public LiveData<DogProfile> getDogProfile() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final MutableLiveData<DogProfile> dogNameMLD = new MutableLiveData<>();

        if(user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w(Utils.TAG, "Listen failed.", e);
                    dogNameMLD.postValue(null);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    DogProfile dogProfile = snapshot.toObject(DogProfile.class);
                    dogNameMLD.postValue(dogProfile);
                } else {
                    Log.d(Utils.TAG, "Current data: null");
                    dogNameMLD.postValue(null);
                }

            });
        }
        else {
            dogNameMLD.postValue(null);
        }

        return dogNameMLD;
    }
}