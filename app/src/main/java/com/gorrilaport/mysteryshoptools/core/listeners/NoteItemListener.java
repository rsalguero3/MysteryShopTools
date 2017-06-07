package com.gorrilaport.mysteryshoptools.core.listeners;


import com.gorrilaport.mysteryshoptools.model.Note;

public interface NoteItemListener {

    void onNoteClick(Note clickedNote);

    void onDeleteButtonClicked(Note clickedNote);
}
