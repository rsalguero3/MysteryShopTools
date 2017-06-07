package com.gorrilaport.mysteryshoptools.core.events;

import android.support.annotation.Nullable;

public class ViewNoteDetailsEvent{
    private long mNoteId;

    public ViewNoteDetailsEvent(@Nullable long noteId){
        mNoteId = noteId;
    }


    public long getNoteId() {
        return mNoteId;
    }

    public void setNote(long noteId) {
        mNoteId = noteId;
    }
}
