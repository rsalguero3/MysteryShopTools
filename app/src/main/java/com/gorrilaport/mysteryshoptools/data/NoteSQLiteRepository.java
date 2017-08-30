package com.gorrilaport.mysteryshoptools.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class NoteSQLiteRepository implements NoteListContract.Repository{


    private CategorySQLiteRepository categorySQLiteRepository;
    private final static String LOG_TAG = NoteSQLiteRepository.class.getSimpleName();
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public NoteSQLiteRepository(Context context) {
        categorySQLiteRepository = new CategorySQLiteRepository(context);
        databaseHelper = DatabaseHelper.newInstance(context);
        database = databaseHelper.getWritableDatabase();
    }

    @Override
    public void addAsync(Note note, OnDatabaseOperationCompleteListener listener) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_NEXT_REMINDER, note.getNextReminder());
        values.put(Constants.COLUMN_LOCAL_AUDIO_PATH, note.getLocalAudioPath());
        values.put(Constants.COLUMN_LOCAL_IMAGE_PATH, note.getLocalImagePath());
        values.put(Constants.COLUMN_LOCAL_SKETCH_PATH, note.getLocalSketchImagePath());
        values.put(Constants.COLUMN_CATEGORY_NAME, note.getCategoryName());
        values.put(Constants.COLUMNS_NOTE_TYPE, note.getNoteType());
        values.put(Constants.COLUMN_CREATED_TIME, System.currentTimeMillis());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());

        if (!TextUtils.isEmpty(note.getCategoryName())){
            values.put(Constants.COLUMNS_CATEGORY_ID, categorySQLiteRepository.createOrGetCategoryId(note.getCategoryName()));
        }

        try {
            long result = database.insertOrThrow(Constants.NOTES_TABLE, null, values);
            listener.onSaveOperationSucceeded(result);
        } catch (SQLiteException e){
            listener.onSaveOperationFailed(e.getLocalizedMessage());
        }
    }

    @Override
    public void updateAsync(Note note, OnDatabaseOperationCompleteListener listener) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_NEXT_REMINDER, note.getNextReminder());
        values.put(Constants.COLUMN_LOCAL_AUDIO_PATH, note.getLocalAudioPath());
        values.put(Constants.COLUMN_LOCAL_IMAGE_PATH, note.getLocalImagePath());
        values.put(Constants.COLUMN_LOCAL_SKETCH_PATH, note.getLocalSketchImagePath());
        values.put(Constants.COLUMN_CATEGORY_NAME, note.getCategoryName());
        values.put(Constants.COLUMNS_CATEGORY_ID, categorySQLiteRepository.createOrGetCategoryId(note.getCategoryName()));
        values.put(Constants.COLUMNS_NOTE_TYPE, note.getNoteType());
        //values.put(Constants.COLUMN_CREATED_TIME, System.currentTimeMillis());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        //Now update the this row with the information supplied

        int result =  database.update(Constants.NOTES_TABLE, values,
                Constants.COLUMN_ID + " = " + note.getId(), null);
        if (result == 1){
            listener.onUpdateOperationCompleted("Updated");
        }else{
            listener.onUpdateOperationFailed("Update Failed");
        }

    }

    @Override
    public void deleteAsync(Note note, OnDatabaseOperationCompleteListener listener) {

        // Ensure database exists.
        if (database != null) {
            int result = database.delete(Constants.NOTES_TABLE, Constants.COLUMN_ID + " = " + note.getId(), null);

            if (result > 0) {
                listener.onDeleteOperationCompleted("Deleted");
            } else {
                listener.onDeleteOperationCompleted("Unable to Delete Note");
            }
        }
    }


    @Override
    public List<Note> getAllNotes(String sortOption, boolean sortOrder) {
        //initialize an empty list of notes
        List<Note> notes = new ArrayList<>();

        //sql command to select all Notes;
        String selectQuery;
        if(sortOrder) {
            selectQuery = "SELECT * FROM " + Constants.NOTES_TABLE + " ORDER BY " + sortOption + " ASC";
        }
        else {
            selectQuery = "SELECT * FROM " + Constants.NOTES_TABLE + " ORDER BY " + sortOption + " DESC";
        }

        //make sure the database is not empty
        if (database != null) {

            //get a cursor for all notes in the database
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    //get each note in the cursor
                    notes.add(Note.fromCursor(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return notes;
    }

    @Override
    public Note getNoteById(long id) {
        //Get the cursor representing the Note
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE + " WHERE " +
                Constants.COLUMN_ID + " = '" + id + "'", null);

        //Create a variable of data type Note
        Note note;
        if (cursor.moveToFirst()){
            note = Note.fromCursor(cursor);
        }else {
            note = null;
        }
        cursor.close();
        //Return result: either a valid note or null
        return  note;
    }
}
