package com.gorrilaport.mysteryshoptools.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class FirebaseRepository implements NoteListContract.FirebaseRepository {

    private DatabaseReference mDatabase;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mNoteCloudReferenece;
    private DatabaseReference mCategoryCloudReferenece;
    private StorageReference mImagesStorageReference;
    private StorageReference mFirebaseStorageReference;

    @Inject NoteListContract.Repository noteSQLiteRepository;


    public FirebaseRepository(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_BUCKET);
        mNoteCloudReferenece = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_END_POINT);
        mCategoryCloudReferenece = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.CATEGORY_CLOUD_END_POINT);
        mImagesStorageReference = mFirebaseStorageReference.child("users/" + mFirebaseUser.getUid() + "attachments");
        MysteryShopTools.getInstance().getAppComponent().inject(this);
    }

    @Override
    public String addNote(Note note) {
        //add note to firebase
        String key = mDatabase.push().getKey();
        note.setFirebaseId(key);
        mNoteCloudReferenece.child(key).setValue(note);

        return key;
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

    @Override
    public void syncToFirebase(final List<Note> notes, final OnDatabaseOperationCompleteListener listener) {

        mNoteCloudReferenece.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //if datasnapshot exists notes in firebase have been added
                if (dataSnapshot.exists()) {
                    //create an array holding firebase notes
                    ArrayList<Note> firebaseNotesArray = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Note n = snapshot.getValue(Note.class);
                        firebaseNotesArray.add(n);
                    }

                    //sqlite notes array is empty, no need to check if notes are in firebase. Add all notes in firebase into sqlite
                    if (notes.isEmpty()) {
                        for (Note note : firebaseNotesArray) {
                            //re add notes from firebase into sqlite
                            long result = noteSQLiteRepository.addAsync(note);
                            note.setId(result);
                            dataSnapshot.getRef().child(note.getFirebaseId()).child("id").setValue(result);
                        }
                    }
                    else {
                        //notes in sqlite
                        Iterator<Note> iter = notes.iterator();
                        //iterate through sql notes and check if they exist in firebase database
                        while (iter.hasNext()) {
                            Note note = iter.next();
                            //note was never added to firebase. add note to firebase
                            if (note.getFirebaseId() == null) {
                                String key = addNote(note);
                                note.setFirebaseId(key);
                                noteSQLiteRepository.updateAsync(note);
                                iter.remove();
                            }
                            //iterate through notes in firebase
                            Iterator<Note> iterFirebase = firebaseNotesArray.iterator();
                            Loop:
                            while (iterFirebase.hasNext()) {
                                Note firebaseNote = iterFirebase.next();
                                if (note.getFirebaseId().equals(firebaseNote.getFirebaseId())) {
                                    //if note is found remove from both arrays and update note if needed
                                    iter.remove();
                                    iterFirebase.remove();
                                    if (note.getDateModified() < firebaseNote.getDateModified()) {
                                        noteSQLiteRepository.updateAsync(firebaseNote);
                                    } else {
                                        dataSnapshot.getRef().child(firebaseNote.getFirebaseId()).setValue(note);
                                    }
                                    break Loop;
                                }
                            }
                        }

                        //add all notes left over in firebase array. These notes were note found in sqlite.
                        if (!firebaseNotesArray.isEmpty()) {
                            for (Note firebaseNote : firebaseNotesArray) {
                                System.out.println("adding note to sql in sync firebase method");
                                long result = noteSQLiteRepository.addAsync(firebaseNote);
                                firebaseNote.setId(result);
                                dataSnapshot.getRef().child(firebaseNote.getFirebaseId()).child("id").setValue(result);
                            }
                        }
                    }
                }

                //All notes in sqlite array not found in firebase database.
                if (!notes.isEmpty()) {
                    for (Note note : notes) {
                        String key = addNote(note);
                        note.setFirebaseId(key);
                        noteSQLiteRepository.updateAsync(note);
                    }
                }
                listener.onSaveOperationSucceeded(Constants.FIREBASE_SYNC_COMPLETED);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateNote(Note note){
        mNoteCloudReferenece.child(note.getFirebaseId()).setValue(note);
    }


    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    }
