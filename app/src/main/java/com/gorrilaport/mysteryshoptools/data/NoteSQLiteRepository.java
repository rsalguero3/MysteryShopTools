package com.gorrilaport.mysteryshoptools.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class NoteSQLiteRepository implements NoteListContract.Repository{


    private CategorySQLiteRepository categorySQLiteRepository;
    private final static String LOG_TAG = NoteSQLiteRepository.class.getSimpleName();
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Inject NoteListContract.FirebaseRepository firebaseRepository;

    public NoteSQLiteRepository(Context context) {
        categorySQLiteRepository = new CategorySQLiteRepository(context);
        databaseHelper = DatabaseHelper.newInstance(context);
        database = databaseHelper.getWritableDatabase();
        MysteryShopTools.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void addAsync(Note note, OnDatabaseOperationCompleteListener listener) {
        Long noteId = null;
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_FIREBASE_ID, note.getFirebaseId());
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_NEXT_REMINDER, note.getNextReminder());
        values.put(Constants.COLUMN_LOCAL_AUDIO_PATH, note.getLocalAudioPath());
        values.put(Constants.COLUMN_LOCAL_SKETCH_PATH, note.getLocalSketchImagePath());
        values.put(Constants.COLUMN_CATEGORY_NAME, note.getCategoryName());
        values.put(Constants.COLUMNS_NOTE_TYPE, note.getNoteType());
        values.put(Constants.COLUMN_CREATED_TIME, System.currentTimeMillis());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        values.put(Constants.COLUMN_COLOR, note.getColor());

        if (!TextUtils.isEmpty(note.getCategoryName())){
            values.put(Constants.COLUMNS_CATEGORY_ID, categorySQLiteRepository.createOrGetCategoryId(note.getCategoryName()));
        }

        try {
            long result = database.insertOrThrow(Constants.NOTES_TABLE, null, values);
            noteId = result;
        } catch (SQLiteException e){
            listener.onSaveOperationFailed(e.getLocalizedMessage());
        }

        ContentValues imagesValues = new ContentValues();
        ArrayList<String> images = note.getImages();
        if (!(images == null)) {
            for (String path : images) {
                imagesValues.put(Constants.COLUMN_IMAGE_PATH, path);
                imagesValues.put(Constants.COLUMN_NOTE_ID, noteId);
                try {
                    long result = database.insertOrThrow(Constants.IMAGE_TABLE, null, imagesValues);
                } catch (SQLiteException e) {
                    listener.onSaveOperationFailed(e.getLocalizedMessage());
                }
            }
        }
        if (noteId != null){
            listener.onSaveOperationSucceeded(noteId);
        }
    }

    @Override
    public Long addAsync(Note note) {
        Long noteId = null;
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_FIREBASE_ID, note.getFirebaseId());
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_NEXT_REMINDER, note.getNextReminder());
        values.put(Constants.COLUMN_LOCAL_AUDIO_PATH, note.getLocalAudioPath());
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
            noteId = result;
        } catch (SQLiteException e){


        }

        ContentValues imagesValues = new ContentValues();
        ArrayList<String> images = note.getImages();
        if (!(images == null)) {
            for (String path : images) {
                imagesValues.put(Constants.COLUMN_IMAGE_PATH, path);
                imagesValues.put(Constants.COLUMN_NOTE_ID, noteId);
                try {
                    long result = database.insertOrThrow(Constants.IMAGE_TABLE, null, imagesValues);
                } catch (SQLiteException e) {

                }
            }
        }

        if (noteId != null){

        }
        return noteId;
    }

    @Override
    public void updateAsync(Note note, OnDatabaseOperationCompleteListener listener, ArrayList<String> newImages) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_FIREBASE_ID, note.getFirebaseId());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_NEXT_REMINDER, note.getNextReminder());
        values.put(Constants.COLUMN_LOCAL_AUDIO_PATH, note.getLocalAudioPath());
        values.put(Constants.COLUMN_LOCAL_SKETCH_PATH, note.getLocalSketchImagePath());
        values.put(Constants.COLUMN_CATEGORY_NAME, note.getCategoryName());
        values.put(Constants.COLUMNS_CATEGORY_ID, categorySQLiteRepository.createOrGetCategoryId(note.getCategoryName()));
        values.put(Constants.COLUMNS_NOTE_TYPE, note.getNoteType());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        //Now update the this row with the information supplied

        int result =  database.update(Constants.NOTES_TABLE, values,
                Constants.COLUMN_ID + " = " + note.getId(), null);

        ContentValues imagesValues = new ContentValues();
        ArrayList<String> images = newImages;
        if (!(images == null)) {
            for (String path : images) {
                imagesValues.put(Constants.COLUMN_IMAGE_PATH, path);
                imagesValues.put(Constants.COLUMN_NOTE_ID, note.getId());
                try {
                    database.insertOrThrow(Constants.IMAGE_TABLE, null, imagesValues);
                } catch (SQLiteException e) {
                    listener.onSaveOperationFailed(e.getLocalizedMessage());
                }
            }
        }
        if (result == 1){
            listener.onUpdateOperationCompleted("Updated");
        }else{
            listener.onUpdateOperationFailed("Update Failed");
        }
    }

    @Override
    public void updateAsync(Note note) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_FIREBASE_ID, note.getFirebaseId());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_NEXT_REMINDER, note.getNextReminder());
        values.put(Constants.COLUMN_LOCAL_AUDIO_PATH, note.getLocalAudioPath());
        values.put(Constants.COLUMN_LOCAL_SKETCH_PATH, note.getLocalSketchImagePath());
        values.put(Constants.COLUMN_CATEGORY_NAME, note.getCategoryName());
        values.put(Constants.COLUMNS_CATEGORY_ID, categorySQLiteRepository.createOrGetCategoryId(note.getCategoryName()));
        values.put(Constants.COLUMNS_NOTE_TYPE, note.getNoteType());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        //Now update the this row with the information supplied

        int result =  database.update(Constants.NOTES_TABLE, values,
                Constants.COLUMN_ID + " = " + note.getId(), null);
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
        if(note.getImages() != null){
            for(String image : note.getImages()){
                deleteAsyncImage(image);
            }
        }
        if(note.getLocalAudioPath() != null){
            deleteAsyncAudio(note.getLocalAudioPath());
        }
    }

    @Override
    public void deleteAsyncImage(String imagePath){

        //ensure database exists.
        if (database != null){
            int result = database.delete(Constants.IMAGE_TABLE, Constants.COLUMN_IMAGE_PATH + "='" + imagePath + "'", null);
            File file = new File(imagePath);
            file.delete();
        }
    }

    @Override
    public void deleteAsyncAudio(String audioPath, OnDatabaseOperationCompleteListener listener) {

        //ensure database exists.
        if (database != null){
            ContentValues values = new ContentValues();
            values.putNull(Constants.COLUMN_LOCAL_AUDIO_PATH);
            int result = database.update(Constants.NOTES_TABLE, values, Constants.COLUMN_LOCAL_AUDIO_PATH + " = '" + audioPath + "'", null);
            File file = new File(audioPath);
            file.delete();

            if (result == 1){
                listener.onUpdateOperationCompleted("Deleted Audio");
            }else{
                listener.onUpdateOperationFailed("Delete Failed");
            }
        }
    }

    @Override
    public void deleteAsyncAudio(String audioPath) {

        //ensure database exists.
        if (database != null){
            ContentValues values = new ContentValues();
            values.putNull(Constants.COLUMN_LOCAL_AUDIO_PATH);
            int result = database.update(Constants.NOTES_TABLE, values, Constants.COLUMN_LOCAL_AUDIO_PATH + " = '" + audioPath + "'", null);
            File file = new File(audioPath);
            file.delete();
        }
    }

    @Override
    public void deleteAllNotesFromCategory(String category) {
        if(database != null){

            database.delete(Constants.NOTES_TABLE, Constants.COLUMN_CATEGORY_NAME + "='" + category + "'", null);
        }

    }

    @Override
    public void updateNotesFromCategory(Category category){
        String query = "SELECT * FROM " + Constants.NOTES_TABLE + " WHERE " + Constants.COLUMN_CATEGORY_NAME
                + " ='" + category + "'";
        if(database != null){
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    Note note = Note.fromCursor(cursor);
                    note.setCategoryName(category.getTitle());
                    note.setColor(category.getColor());
                    updateAsync(note);
                }
            }
            cursor.close();
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
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    //get each note in the cursor
                    Note note = Note.fromCursor(cursor);
                    //Get the image path from cursor
                    String query = "SELECT " + Constants.COLUMN_IMAGE_PATH + " FROM " + Constants.IMAGE_TABLE + " WHERE "
                            + Constants.COLUMN_NOTE_ID + " = " + note.getId();
                    Cursor image = database.rawQuery(query, null);
                    ArrayList<String> imagesArray = new ArrayList<>();
                    if (image.moveToFirst()){
                        while(!image.isAfterLast()){
                            imagesArray.add(image.getString(image.getColumnIndex(Constants.COLUMN_IMAGE_PATH)));
                            image.moveToNext();
                        }
                    }
                    image.close();
                    note.setImages(imagesArray);
                    notes.add(note);
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
                    //Get the image path from cursor
                    String query = "SELECT " + Constants.COLUMN_IMAGE_PATH + " FROM " + Constants.IMAGE_TABLE + " WHERE "
                            + Constants.COLUMN_NOTE_ID + " = " + note.getId();
                    Cursor image = database.rawQuery(query, null);
                    ArrayList<String> imagesArray = new ArrayList<>();
                    if (image.moveToFirst()){
                        while(!image.isAfterLast()){
                            imagesArray.add(image.getString(image.getColumnIndex(Constants.COLUMN_IMAGE_PATH)));
                            image.moveToNext();
                        }
                        image.close();
                        note.setImages(imagesArray);
                    }

        }else {
            note = null;
        }
        cursor.close();
        //Return result: either a valid note or null
        return  note;
    }

    @Override
    public void deleteDatabase(){
        List<Note> notes = getAllNotes("title", true);
        if(firebaseRepository.getFirebaseUser() != null){
            for(Note note: notes){
                System.out.println(note.getTitle());
                firebaseRepository.deleteNote(note);
            }
        }
        //databaseHelper.deleteDatabase();
    }

    @Override
    public SQLiteDatabase getDatabase(){
        return database;
    }
}
