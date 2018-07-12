package com.gorrilaport.mysteryshoptools.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryListContract;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;


import java.io.File;
import java.util.ArrayList;
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
    private StorageReference mAudioStorageReference;

    @Inject NoteListContract.Repository noteSQLiteRepository;
    @Inject CategoryListContract.Repository categoryRepository;
    @Inject Context mContext;


    public FirebaseRepository(){
        updateUserInfo();
    }

    public void updateUserInfo(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            mFirebaseStorage = FirebaseStorage.getInstance();
            mFirebaseStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_BUCKET);
            mNoteCloudReferenece = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_END_POINT);
            mCategoryCloudReferenece = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.CATEGORY_CLOUD_END_POINT);
            mImagesStorageReference = mFirebaseStorageReference.child("users/" + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_IMAGE_END_POINT + Constants.NOTE_CLOUD_END_POINT);
            mAudioStorageReference = mFirebaseStorageReference.child("users/" + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_AUDIO_END_POINT + Constants.NOTE_CLOUD_END_POINT);
            MysteryShopTools.getInstance().getAppComponent().inject(this);
        }
    }

    @Override
    public String addNote(Note note) {
        //add note to firebase
        String key = mDatabase.push().getKey();
        note.setFirebaseId(key);
        mNoteCloudReferenece.child(key).setValue(note);
        addImages(note);

        return key;
    }

    @Override
    public void addAudio(Note note) {
        if (note.getLocalAudioPath() != null) {
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("audio/mpeg")
                    .build();
            String filePath = note.getLocalAudioPath();
            Uri fileToUpload = Uri.fromFile(new File(filePath));
            final String fileName = fileToUpload.getLastPathSegment();
            StorageReference audioRef = mAudioStorageReference.child(note.getFirebaseId()).child(fileName);
            UploadTask uploadTask = audioRef.putFile(fileToUpload, metadata);
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
    public void addImages(Note note) {
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(".jpeg")
                    .build();
            if (note.getImages() != null) {
                ArrayList<String> images = note.getImages();
                for (String filePath : images) {
                    Uri fileToUpload = Uri.fromFile(new File(filePath));

                    final String fileName = fileToUpload.getLastPathSegment();

                    StorageReference imageRef = mImagesStorageReference.child(note.getFirebaseId()).child(fileName);

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
    }

    public void downloadImages(Note note){
        if (note.getImages() != null) {
            ArrayList<String> images = note.getImages();
            for (String filePath : images) {
                Uri fileToDownload = Uri.fromFile(new File(filePath));

                final String fileName = fileToDownload.getLastPathSegment();
                try {
                    File localFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + fileName);
                    StorageReference imageRef = mImagesStorageReference.child(note.getFirebaseId()).child(fileName);

                    imageRef.getFile(localFile).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
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
                            categoryRepository.createOrGetCategoryId(note.getCategoryName());
                            long result = noteSQLiteRepository.addAsync(note);
                            note.setId(result);
                            dataSnapshot.getRef().child(note.getFirebaseId()).child("id").setValue(result);
                            downloadImages(note);
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
                                if(note.getImages() != null){
                                    addImages(note);
                                    addAudio(note);
                                }
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
                                categoryRepository.createOrGetCategoryId(firebaseNote.getCategoryName());
                                long result = noteSQLiteRepository.addAsync(firebaseNote);
                                firebaseNote.setId(result);
                                dataSnapshot.getRef().child(firebaseNote.getFirebaseId()).child("id").setValue(result);
                                downloadImages(firebaseNote);
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
                        addImages(note);
                        addAudio(note);
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
        addImages(note);
        addAudio(note);
    }

    @Override
    public void deleteImage(String path){
        //Query image table and find the row with the same path name under image_path column
        Uri file = Uri.fromFile(new File(path));
        final String fileName = file.getLastPathSegment();
        String query = "SELECT " + "*" + " FROM " + Constants.IMAGE_TABLE + " WHERE "
                + Constants.COLUMN_IMAGE_PATH + " = '" + path + "'";
        Cursor cursor = noteSQLiteRepository.getDatabase().rawQuery(query, null);

        if (cursor.moveToFirst()){
            //find the note that us associated with the image
            Note note = noteSQLiteRepository.getNoteById(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_NOTE_ID)));
            mImagesStorageReference.child(note.getFirebaseId()).child(fileName).delete();

        }
        cursor.close();
    }

    @Override
    public void deleteAudio(Note note) {
        String path = note.getLocalAudioPath();
        Uri file = Uri.fromFile(new File(path));
        final String fileName = file.getLastPathSegment();
        mAudioStorageReference.child(note.getFirebaseId()).child(fileName).delete();
    }

    @Override
    public void deleteNote(Note note){
        System.out.println("firebase delete called");
        ArrayList<String> images = note.getImages();
        System.out.println(mNoteCloudReferenece);
        mNoteCloudReferenece.child(note.getFirebaseId()).removeValue();
        if (images != null && !images.isEmpty()) {
            for (String path : images) {
                deleteImage(path);
            }
        }
        if (note.getLocalAudioPath() != null){
            deleteAudio(note);
        }
    }

    @Override
    public void deleteAllNotesFromCategory(String category){
        mNoteCloudReferenece.orderByChild("categoryName").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Note n = snapshot.getValue(Note.class);
                        deleteNote(n);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    }
