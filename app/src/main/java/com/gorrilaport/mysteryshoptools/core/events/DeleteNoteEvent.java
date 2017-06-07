package com.gorrilaport.mysteryshoptools.core.events;

import com.gorrilaport.mysteryshoptools.model.Note;

public class DeleteNoteEvent{
    private Note mNote;

    public DeleteNoteEvent(Note note){
        mNote = note;
    }

    public Note getNote() {
        return mNote;
    }

    public void setNote(Note note) {
        mNote = note;
    }
}
