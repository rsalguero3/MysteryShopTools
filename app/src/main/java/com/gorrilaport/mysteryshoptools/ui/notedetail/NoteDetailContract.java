package com.gorrilaport.mysteryshoptools.ui.notedetail;

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
