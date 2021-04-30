package uk.ac.abertay.cmp309.dogtracker.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import uk.ac.abertay.cmp309.dogtracker.DogProfile;
import uk.ac.abertay.cmp309.dogtracker.MainActivity;
import uk.ac.abertay.cmp309.dogtracker.ProfileActivity;
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
                    Log.d(Utils.TAG, "Current data: " + snapshot.getData());
                    DogProfile dogProfile = snapshot.toObject(DogProfile.class);
                    dogNameMLD.postValue(dogProfile);
                } else {
                    Log.d(Utils.TAG, "Current data: null");
                    dogNameMLD.postValue(null);
                }

            });
            /*docRef.get().addOnCompleteListener(task -> {
                Log.d(Utils.TAG, "Task Complete");
                if (task.isSuccessful()) {
                    Log.d(Utils.TAG, "Task Successful");
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        DogProfile dogProfile = document.toObject(DogProfile.class);
                        dogNameMLD.postValue(dogProfile);
                    } else {
                        Log.e(Utils.TAG, "Error -- Document does NOT exist");
                        dogNameMLD.postValue(null);
                    }
                } else {
                    Log.e(Utils.TAG, "Task failed with exception: ", task.getException());
                    dogNameMLD.postValue(null);
                }
            });*/
        }
        else {
            dogNameMLD.postValue(null);
        }

        return dogNameMLD;
    }
}