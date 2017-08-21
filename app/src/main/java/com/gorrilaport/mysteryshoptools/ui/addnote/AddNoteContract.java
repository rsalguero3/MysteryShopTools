package com.gorrilaport.mysteryshoptools.ui.addnote;


import android.content.Context;

import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.model.Note;

import java.util.List;

public interface AddNoteContract {

    interface View{
        void populateNote(Note note);
        void setCurrentNote(Note note);
        void displayCategory(String category);
        void displayDeleteConfirmation(Note note);
        void displayDiscardConfirmation();
        void displayPreviousActivity();
        void displayMessage(String message);
        void displayChooseCategoryDialog(List<Category> categories);
        Context getContext();
        void finish();
    }

    interface Action{
        void onAddClick(Note note);
        void checkStatus();
        void onDeleteNoteButtonClicked();
        void deleteNote();
        void saveOnExit(Note note);
        void onSelectCategory();
    }
}
