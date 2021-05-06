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

//Model for the health fragment
//This will gather all the data needed for the controller/view
public class HomeViewModel extends ViewModel {


    public HomeViewModel() {
        //Constructor
    }


    //GetDogProfile Method -- This retrieves the data from FireStore and returns it
    //as a DogProfile class for ease of access to the data
    public LiveData<DogProfile> getDogProfile() {

        //Initialise Firebase data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //Initialise DogProfile variable
        final MutableLiveData<DogProfile> dogNameMLD = new MutableLiveData<>();

        //Check if user is valid
        if(user != null) {

            //If user is valid, get data from document
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {

                    //Error, listen failed
                    Log.w(Utils.TAG, "Listen failed.", e);
                    dogNameMLD.postValue(null);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    //Success, post data to DogProfile variable
                    DogProfile dogProfile = snapshot.toObject(DogProfile.class);
                    dogNameMLD.postValue(dogProfile);

                } else {

                    //Error, Document does not exist
                    Log.d(Utils.TAG, "Current data: null");
                    dogNameMLD.postValue(null);
                }

            });
        }
        else {

            //Error, User not logged in
            dogNameMLD.postValue(null);
        }

        //Return dogProfile
        return dogNameMLD;
    }
}