package com.gorrilaport.mysteryshoptools.ui.notedetail;

import android.content.Intent;

import com.gorrilaport.mysteryshoptools.model.Note;

public interface NoteDetailContract{


   interface View{
      void displayNote(Note note);
      void showEditNoteScreen(long noteId);
      void showDeleteConfirmation(Note note);
      void displayShareIntent();
      void displayPreviousActivity();
      void showMessage(String message);
      void displayReadOnlyViews();
      void finish();
      void promptForDelete(Note note);
      void displayFullImage(String imagePath);

   }

   interface Action{
      void onEditNoteClick();
      void showNoteDetails();
      void onDeleteNoteButtonClicked();
      void onShareButtonClicked();
      void onPlayAudioButtonClicked();
      void deleteNote();
      Note getCurrentNote();

   }






}
