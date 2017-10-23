package com.gorrilaport.mysteryshoptools.model;

import android.database.Cursor;

import com.gorrilaport.mysteryshoptools.util.Constants;

import java.util.ArrayList;

public class Note {

    private long id;
    private String firebaseId;
    private String title;
    private String content;
    private long nextReminder;
    private String localAudioPath;
    private String localSketchImagePath;
    private String categoryName;
    private long categoryId;
    private String noteType;
    private long dateCreated;
    private long dateModified;
    private ArrayList<String> images;

    public Note(){}

    public static Note fromCursor(Cursor cursor){
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_ID)));
        note.setFirebaseId(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_FIREBASE_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_CONTENT)));
        note.setNextReminder(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_NEXT_REMINDER)));
        note.setLocalAudioPath(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_LOCAL_AUDIO_PATH)));
        note.setLocalSketchImagePath(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_LOCAL_SKETCH_PATH)));
        note.setCategoryName(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_CATEGORY_NAME)));
        note.setCategoryId(cursor.getLong(cursor.getColumnIndex(Constants.COLUMNS_CATEGORY_ID)));
        note.setNoteType(cursor.getString(cursor.getColumnIndex(Constants.COLUMNS_NOTE_TYPE)));
        note.setDateCreated(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_CREATED_TIME)));
        note.setDateModified(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_MODIFIED_TIME)));
        return note;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirebaseId(){ return firebaseId; }

    public void setFirebaseId(String id){ this.firebaseId = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getNextReminder() {
        return nextReminder;
    }

    public void setNextReminder(long nextReminder) {
        this.nextReminder = nextReminder;
    }

    public String getLocalAudioPath() {
        return localAudioPath;
    }

    public void setLocalAudioPath(String localAudioPath) {
        this.localAudioPath = localAudioPath;
    }

    public String getLocalSketchImagePath() {
        return localSketchImagePath;
    }

    public void setLocalSketchImagePath(String localSketchImagePath) {
        this.localSketchImagePath = localSketchImagePath;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getImages(){
        return this.images;
    }
}
