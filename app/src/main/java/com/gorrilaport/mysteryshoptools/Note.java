package com.gorrilaport.mysteryshoptools;

import android.database.Cursor;
import android.text.format.DateFormat;

import java.util.UUID;

/**
 * Created by Ricardo on 3/13/2016.
 */
public class Note {
    private long id;
    private String mTitle;
    private String mTextInput;
    private DateFormat mDate;

    public Note(){
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }


    public void setTextInput(String textInput){
        this.mTextInput = textInput;
    }

    public String getTextInput(){
        return mTextInput;
    }

    public DateFormat getDate(){
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getId() {
        return id;
    }

    public static Note getNoteFromCursor(Cursor cursor) {
        Note note = new Note();
        note.setTitle(cursor.getString(cursor.getColumnIndex(NotePadDbSchema.NotesTable.Cols.TITLE)));
        note.setTextInput(cursor.getString(cursor.getColumnIndex(NotePadDbSchema.NotesTable.Cols.TEXT)));

        return note;
    }
}
