package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.content.Context;
import android.database.Cursor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Note;

import java.util.ArrayList;
import java.util.List;

public interface NoteListContract {

    interface View {

        void showNotes(List<Note> notes);
        void showAddNote();
        Context getContext();
        void showSingleDetailUi(long noteId);
        void showDualDetailUi(Note note);
        void showEmptyText(boolean showText);
        void showDeleteConfirmation(Note note);
        void showMessage(String message);

    }

    interface Actions {
        void loadNotes();
        void onAddNewNoteButtonClicked();
        void openNoteDetails(long noteId);
        List<Note> getNotes(String sortColumn, boolean ascending);
        void onDeleteNoteButtonClicked(Note note);
        void deleteNote(Note note);
        void setLayoutMode(boolean dualScreen);
        void syncToFirebaseButtonClicked();
    }

    interface Repository{
        void addAsync(Note note, OnDatabaseOperationCompleteListener listener);
        Long addAsync(Note note);
        void updateAsync(Note note, OnDatabaseOperationCompleteListener listener, ArrayList<String> newImages);
        void updateAsync(Note note);
        void deleteAsync(Note note, OnDatabaseOperationCompleteListener listener);
        void deleteAsyncImage(String imagePath);
        void deleteDatabase();
        List<Note> getAllNotes(String sortOption, boolean sortOrder);
        Note getNoteById(long id);
    }

    interface FirebaseRepository {
        String addNote(Note note);
        void addImages(ArrayList<String> images);
        void updateNote(Note note);
        void syncToFirebase(List<Note> notes, OnDatabaseOperationCompleteListener listener);
        FirebaseUser getFirebaseUser();
        FirebaseAuth getFirebaseAuth();
    }
}
