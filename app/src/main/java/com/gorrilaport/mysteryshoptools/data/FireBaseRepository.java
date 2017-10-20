package com.gorrilaport.mysteryshoptools.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;

public class FireBaseRepository implements NoteListContract.FireBaseRepository {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;


    private FirebaseUser mFirebaseUser;

    public FireBaseRepository(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public void addNote(Note note) {
            System.out.println("adding note to firebase");
            System.out.println(mFirebaseUser.toString());
            System.out.println(mFirebaseUser ==  null );
            System.out.println(mFirebaseUser.getDisplayName());
    }

    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }
}
