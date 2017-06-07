package com.gorrilaport.mysteryshoptools.ui.notedetail;


import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;

import javax.inject.Inject;

public class NoteDetailPresenter implements NoteDetailContract.Action, OnDatabaseOperationCompleteListener {


    @Inject
    NoteListContract.Repository mRepository;
    private final NoteDetailContract.View mView;
    private long noteId;

    public NoteDetailPresenter(NoteDetailContract.View mView, long noteId) {
        this.mView = mView;
        this.noteId = noteId;
        MysteryShopTools.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void onEditNoteClick() {
        mView.showEditNoteScreen(noteId);
    }

    @Override
    public void showNoteDetails() {
        Note selectedNote = mRepository.getNoteById(noteId);
        mView.displayNote(selectedNote);
    }

    @Override
    public void onDeleteNoteButtonClicked() {
        mView.showDeleteConfirmation(mRepository.getNoteById(noteId));
    }

    @Override
    public void onShareButtonClicked() {
        mView.displayShareIntent();
    }

    @Override
    public void onPlayAudioButtonClicked() {

    }

    @Override
    public void deleteNote() {
        mRepository.deleteAsync(mRepository.getNoteById(noteId), this);
    }

    @Override
    public Note getCurrentNote() {
        return mRepository.getNoteById(noteId);
    }


    @Override
    public void onSaveOperationFailed(String error) {
        mView.showMessage(error);
    }

    @Override
    public void onSaveOperationSucceeded(long id) {
        this.noteId = id;
        showNoteDetails();
    }

    @Override
    public void onDeleteOperationCompleted(String message) {
        mView.showMessage(message);
        mView.finish();
    }

    @Override
    public void onDeleteOperationFailed(String error) {
        mView.showMessage(error);

    }

    @Override
    public void onUpdateOperationCompleted(String message) {
        mView.showMessage(message);
        showNoteDetails();

    }

    @Override
    public void onUpdateOperationFailed(String error) {
        mView.showMessage(error);
    }
}