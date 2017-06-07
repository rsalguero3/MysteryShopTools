package com.gorrilaport.mysteryshoptools.core.events;

import com.gorrilaport.mysteryshoptools.model.Note;

public class ShowNoteDualPaneEvent {
    private final Note note;

    public ShowNoteDualPaneEvent(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}
