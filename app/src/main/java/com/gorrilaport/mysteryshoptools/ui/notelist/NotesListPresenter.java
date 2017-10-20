package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Note;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NotesListPresenter implements NoteListContract.Actions, OnDatabaseOperationCompleteListener {

    private final NoteListContract.View mView;
    private boolean isDualScreen = false;

    @Inject NoteListContract.Repository mRepository;
    @Inject SharedPreferences mSharedPreference;
    @Inject NoteListContract.FireBaseRepository mFireBaseRepository;

    public NotesListPresenter(NoteListContract.View notesView){
        mView = notesView;
        MysteryShopTools.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void loadNotes() {
        String sortColumn = mSharedPreference.getString("sort_options", mView.getContext().getString(R.string.column_title));
        String sordOrderValue = mSharedPreference.getString("list_sort_order", "true");
        boolean sortOrder = Boolean.valueOf(sordOrderValue);
        List<Note> notes = getNotes(sortColumn, sortOrder);

        if (notes != null && notes.size() > 0){
            mView.showEmptyText(false);
            mView.showNotes(notes);

        }else {
            mView.showEmptyText(true);
            mView.showNotes(new ArrayList<Note>());
        }
    }


    @Override
    public void onAddNewNoteButtonClicked() {
        mView.showAddNote();
    }

    @Override
    public void openNoteDetails(@NonNull long noteId) {
        if (isDualScreen) {
            mView.showDualDetailUi(mRepository.getNoteById(noteId));
        } else {
            mView.showSingleDetailUi(noteId);
        }
    }

    @Override
    public List<Note> getNotes(String sortColumn, boolean sortOrder) {
        mFireBaseRepository.addNote(new Note());
        return mRepository.getAllNotes(sortColumn, sortOrder);
    }

    @Override
    public void onDeleteNoteButtonClicked(Note note) {
        mView.showDeleteConfirmation(note);
    }

    @Override
    public void deleteNote(Note note) {
        mRepository.deleteAsync(note, this);
    }

    @Override
    public void setLayoutMode(boolean dualScreen) {
        isDualScreen = dualScreen;
    }

    @Override
    public void onSaveOperationFailed(String error) {
        mView.showMessage(error);
    }

    @Override
    public void onSaveOperationSucceeded(long id) {
        mView.showMessage("Saved");
        loadNotes();
    }

    @Override
    public void onDeleteOperationCompleted(String message) {
        mView.showMessage(message);
        loadNotes();
    }

    @Override
    public void onDeleteOperationFailed(String error) {
        mView.showMessage(error);
    }

    @Override
    public void onUpdateOperationCompleted(String message) {
        mView.showMessage(message);
        loadNotes();
    }

    @Override
    public void onUpdateOperationFailed(String error) {
        mView.showMessage(error);
    }
}
