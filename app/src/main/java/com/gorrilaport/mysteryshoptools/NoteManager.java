package com.gorrilaport.mysteryshoptools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 8/29/2016.
 */
public class NoteManager {
    private Context mContext;
    private static NoteManager sNoteManagerInstance = null;

    public static NoteManager newInstance(Context context){

        if (sNoteManagerInstance == null){
            sNoteManagerInstance = new NoteManager(context.getApplicationContext());
        }

        return sNoteManagerInstance;
    }

    private NoteManager(Context context){
        this.mContext = context.getApplicationContext();
    }

    public long create(Note note){
        ContentValues values = new ContentValues();
        values.put(NotePadDbSchema.NotesTable.Cols.TITLE, note.getTitle());
        values.put(NotePadDbSchema.NotesTable.Cols.NOTE_ID, note.getTextInput());
        Uri result = mContext.getContentResolver().insert(NoteProvider.CONTENT_URI, values);
        long id = Long.parseLong(result.getLastPathSegment());
        return id;
    }

    public List<Note> getAllNotes(){
        List<Note> notes = new ArrayList<Note>();
        Cursor cursor = mContext.getContentResolver().query(NoteProvider.CONTENT_URI, NotePadDbSchema.NotesTable.COLUMNS, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                notes.add(Note.getNoteFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }

    public Note getNote(Long id){
        Note note;
        Cursor cursor = mContext.getContentResolver().query(NoteProvider.CONTENT_URI, NotePadDbSchema.NotesTable.COLUMNS, NotePadDbSchema.NotesTable.Cols.NOTE_ID + " = " + id, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            note = Note.getNoteFromCursor(cursor);
            return note;
        }
        return null;
    }

    public void update(Note note){
        ContentValues values = new ContentValues();
        values.put(NotePadDbSchema.NotesTable.Cols.TITLE, note.getTitle());
        values.put(NotePadDbSchema.NotesTable.Cols.TEXT, note.getTextInput());
        mContext.getContentResolver().update(NoteProvider.CONTENT_URI, values, NotePadDbSchema.NotesTable.Cols.NOTE_ID + "=" + note.getId(), null);
    }

    public void delete(Note note){
        mContext.getContentResolver().delete(NoteProvider.CONTENT_URI, NotePadDbSchema.NotesTable.Cols.NOTE_ID + "=" + note.getId(), null);
    }
}
