package com.gorrilaport.mysteryshoptools.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.io.File;
import java.util.ArrayList;

public class FirebaseRepository implements NoteListContract.FirebaseRepository {

    private DatabaseReference mDatabase;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mNoteCloudReferenece;
    private DatabaseReference mCategoryCloudReferenece;
    private StorageReference mImagesStorageReference;
    private StorageReference mFirebaseStorageReference;


    public FirebaseRepository(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_BUCKET);
        mNoteCloudReferenece = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_END_POINT);
        mImagesStorageReference = mFirebaseStorageReference.child("users/" + mFirebaseUser.getUid() + "attachments");
    }

    @Override
    public void addNote(Note note) {
        String key = mDatabase.push().getKey();
        note.setFirebaseId(key);
        mNoteCloudReferenece.child(key).setValue(note);
    }

    @Override
    public void addImages(ArrayList<String> images) {
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(".jpeg")
                    .build();

            for (String filePath : images) {
                Uri fileToUpload = Uri.fromFile(new File(filePath));

                final String fileName = fileToUpload.getLastPathSegment();

                StorageReference imageRef = mImagesStorageReference.child(fileName);

                UploadTask uploadTask = imageRef.putFile(fileToUpload, metadata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //makeToast("Unable to upload file to cloud" + e.getLocalizedMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //makeToast("File uploaded successfully");
                    }
                });

            }
    }


    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

}
