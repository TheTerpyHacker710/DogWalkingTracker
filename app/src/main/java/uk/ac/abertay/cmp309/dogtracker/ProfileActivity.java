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

    private final int PICK_IMAGE_REQUEST = 22;

    private ImageView imageView;
    private TextView imageTextView;
    private Uri filepath;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageTextView = findViewById(R.id.textViewImageDescription);
        imageView = findViewById(R.id.imageViewProfileDogUpload);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public void handleClicks(View view) {
        switch(view.getId()) {
            case R.id.buttonProfileUploadImage:
                selectImage();
                break;
            case R.id.buttonProfileUpdate:
                updateProfile();
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void updateProfile() {
        if (filepath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + currentUser.getUid() + "/" + "profile.jpg");

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            ref.putFile(filepath, metadata).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            }).addOnCompleteListener(task -> { updateDogProfile(); });
        }

        Log.d(Utils.TAG, "User Updated");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);

                imageTextView.setText(filepath.toString());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDogProfile() {
        String dogName = ((EditText) findViewById(R.id.editTextProfileDogsName)).getText().toString();
        int dogAge = Integer.parseInt(((EditText) findViewById(R.id.editTextProfileDogAge)).getText().toString());
        int caloriesPerMeal = Integer.parseInt(((EditText) findViewById(R.id.editTextProfileCaloriesPerMeal)).getText().toString());

        StorageReference storageReference = storage.getReference();

        storageReference.child("images/" + currentUser.getUid() + "/" + "profile.jpg").getDownloadUrl().addOnSuccessListener(uri -> {

            Log.i(Utils.TAG, uri.toString());

            Map<String, Object> dogDetails = new HashMap<>();
            dogDetails.put("caloriesPerMeal", caloriesPerMeal);
            dogDetails.put("dogName", dogName);
            dogDetails.put("dogAge", dogAge);
            dogDetails.put("dogPhotoURL", uri.toString());
            dogDetails.put("profileSet", true);

            db.collection("users").document(currentUser.getUid()).update(dogDetails)
                    .addOnSuccessListener(aVoid -> Log.i(Utils.TAG, "Document Added!"))
                    .addOnFailureListener(e -> Log.e(Utils.TAG, "Error adding document", e));

            finish();
        }).addOnFailureListener(e -> {
            //TODO: handle errors
        });


    }
}