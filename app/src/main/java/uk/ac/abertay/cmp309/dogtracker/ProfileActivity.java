package uk.ac.abertay.cmp309.dogtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileActivity extends AppCompatActivity {

    //Initialise the Image Request Flag
    private final int PICK_IMAGE_REQUEST = 22;

    //Declare Views
    private ImageView imageView;
    private TextView imageTextView;

    //Declare Uri Path
    private Uri filepath;

    //Declare Firebase Variables
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialise the Views
        imageTextView = findViewById(R.id.textViewImageDescription);
        imageView = findViewById(R.id.imageViewProfileDogUpload);

        //Initialise the Firebase Objects
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    //HandleClicks Method
    //This method will handle the clicks of the buttons
    public void handleClicks(View view) {

        //Check to see which buttons were pressed
        switch(view.getId()) {

            //If the Upload Image button was pressed
            case R.id.buttonProfileUploadImage:

                //Select the image to upload
                selectImage();

                break;

            //If it was Update Profile
            case R.id.buttonProfileUpdate:

                //Update the profile
                updateProfile();

                break;
        }
    }

    //SelectImage Method
    //This method will open the users file system to choose an image
    private void selectImage() {

        //Create a new intent to open the file browser to choose an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        //Start activity and wait for result
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    //UpdateProfile Method
    //This method will send the data to the database
    private void updateProfile() {

        //Check to see if the file has been choosen
        if (filepath != null) {
            //Show an upload dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //Get a storage reference to store the image
            StorageReference ref = storageReference.child("images/" + currentUser.getUid() + "/" + "profile.jpg");

            //Build the metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            //Store the image in Firebase Storage
            ref.putFile(filepath, metadata).addOnSuccessListener(taskSnapshot -> {

                //Dismiss the dialog and show a conformation message
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> {

                //Dismiss the dialog and show an error message
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }).addOnProgressListener(snapshot -> {

                //Display the progress
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");

            }).addOnCompleteListener(task -> {

                //Once complete update the profile
                updateDogProfile();

            });
        }

    }

    //OnActivityResult Method
    //This method will wait for the activity to send a result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check the request and result codes
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //Set the filepath of the image
            filepath = data.getData();
            try {
                //Set the imageView of the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);

                //Set the textView
                imageTextView.setText(filepath.toString());
            }
            catch (IOException e) {
                //Catch any errros
                e.printStackTrace();
            }
        }
    }

    //UpdateDogProfile Method
    //This will take all the data and send it to Firestore
    private void updateDogProfile() {

        //Initialise the variables from user input
        String dogName = ((EditText) findViewById(R.id.editTextProfileDogsName)).getText().toString();
        int dogAge = Integer.parseInt(((EditText) findViewById(R.id.editTextProfileDogAge)).getText().toString());
        int caloriesPerMeal = Integer.parseInt(((EditText) findViewById(R.id.editTextProfileCaloriesPerMeal)).getText().toString());

        //Get the storage reference of the image
        StorageReference storageReference = storage.getReference();

        //Get the image URI
        storageReference.child("images/" + currentUser.getUid() + "/" + "profile.jpg").getDownloadUrl().addOnSuccessListener(uri -> {

            //Input all the data into a HashMap to store in FireStore
            Map<String, Object> dogDetails = new HashMap<>();
            dogDetails.put("caloriesPerMeal", caloriesPerMeal);
            dogDetails.put("dogName", dogName);
            dogDetails.put("dogAge", dogAge);
            dogDetails.put("dogPhotoURL", uri.toString());
            dogDetails.put("profileSet", true);

            //Update the FireStore with the user input
            db.collection("users").document(currentUser.getUid()).update(dogDetails)
                    .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                    .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));

            //Return to the previous activity
            finish();

        }).addOnFailureListener(e -> {
            //TODO: handle errors
        });


    }
}