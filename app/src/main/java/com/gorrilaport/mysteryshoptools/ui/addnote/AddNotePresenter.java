package com.gorrilaport.mysteryshoptools.ui.addnote;

import android.content.SharedPreferences;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryListContract;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddNotePresenter implements AddNoteContract.Action, OnDatabaseOperationCompleteListener {

    private final AddNoteContract.View mView;
    @Inject CategoryListContract.Repository mCategoryRepository;
    @Inject NoteListContract.Repository mNoteRepository;
    @Inject NoteListContract.Repository mRepository;
    @Inject SharedPreferences mSharedPreference;
    @Inject NoteListContract.FirebaseRepository mFirebaseRepository;
    private boolean mEditMode = false;
    private Note mCurrentNote = null;

    public AddNotePresenter(AddNoteContract.View addNoteView, long noteId) {
        MysteryShopTools.getInstance().getAppComponent().inject(this);
        mView = addNoteView;
        if (noteId > 0){
            mCurrentNote = mNoteRepository.getNoteById(noteId);
            mEditMode = true;
        }
    }

    @Override
    public void onAddClick(Note note, ArrayList<String> images) {
        // check to see if note is a new note and add to database. If not just update note
        if (note != null && note.getId() > 0){
            //check if user is logged in
            if (mFirebaseRepository.getFirebaseUser() != null){
                mNoteRepository.updateAsync(note, this, images);
                mFirebaseRepository.updateNote(mNoteRepository.getNoteById(note.getId()));
            }
            else {
                mNoteRepository.updateAsync(note, this, images);
            }

        }
        else {
            if (mFirebaseRepository.getFirebaseUser() != null) {
                String key = mFirebaseRepository.addNote(note);
                note.setFirebaseId(key);
                mNoteRepository.addAsync(note, this);
            }
            else {
                mNoteRepository.addAsync(note, this);
            }
        }
    }

    @Override
    public void checkStatus() {
        if (mEditMode && mCurrentNote != null && mCurrentNote.getId() > 0){
            mView.populateNote(mCurrentNote);
            mView.setCurrentNote(mCurrentNote);
        }else {
            mView.setCurrentNote(new Note());
        }
    }

    @Override
    public void onDeleteNoteButtonClicked() {
        if (mEditMode && mCurrentNote != null){
            mView.displayDeleteConfirmation(mCurrentNote);
        }else {
            mView.displayDiscardConfirmation();
        }
    }

    @Override
    public void deleteNote() {
        if (mCurrentNote != null && mCurrentNote.getId() > 0) {
            mNoteRepository.deleteAsync(mCurrentNote, this);
        }
    }

    @Override
    public void deleteAudio() {
        if(mCurrentNote != null){
            Note note = mRepository.getNoteById(mCurrentNote.getId());
            mRepository.deleteAsyncAudio(note.getLocalAudioPath(), this);
        }
        else {
            File file = new File(((NoteEditorFragment) mView).mLocalAudioFilePath);
            file.delete();
        }
        ((NoteEditorFragment) mView).setAudioPlayer(null);
    }

    @Override
    public void saveOnExit(Note note) {
        //onAddClick(note);
    }

    @Override
    public void onSelectCategory() {
        String sortColumn = mSharedPreference.getString("sort_options", mView.getContext().getString(R.string.column_title));
        String sordOrderValue = mSharedPreference.getString("list_sort_order", "true");
        boolean sortOrder = Boolean.valueOf(sordOrderValue);

        List<Category> mCategories = mCategoryRepository.getAllCategories(sortColumn, sortOrder);
        mView.displayChooseCategoryDialog(mCategories);
    }

    @Override
    public void onSaveOperationFailed(String error) {
        mView.displayMessage(error);
    }

    @Override
    public void onSaveOperationSucceeded(long id) {
        mCurrentNote = mNoteRepository.getNoteById(id);
        mView.displayMessage("Saved");
        mEditMode = true;
        checkStatus();
    }

    @Override
    public void onDeleteOperationCompleted(String message) {
        mView.displayMessage(message);

    }

    @Override
    public void onDeleteOperationFailed(String error) {
        mView.displayMessage(error);
    }

    @Override
    public void onUpdateOperationCompleted(String message) {
        mEditMode = true;
        mView.displayMessage(message);
        checkStatus();
    }

    @Override
    public void onUpdateOperationFailed(String error) {
        mView.displayMessage(error);
    }
}
