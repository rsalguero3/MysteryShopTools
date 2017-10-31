package com.gorrilaport.mysteryshoptools.model;

import android.database.Cursor;

import com.gorrilaport.mysteryshoptools.util.Constants;

public class Category{

    private long id;
    private String firebaseId;
    private String title;
    private long dateCreated;
    private long dateModified;

    public Category() {
    }

    public Category(long id, String name){
        this.id = id;
        this.title = name;
    }

    public static Category fromCursor(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TITLE));
        Category category = new Category(id, name);
        category.setDateCreated(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_CREATED_TIME)));
        category.setDateModified(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_MODIFIED_TIME)));
        return category;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirebaseId() { return firebaseId; }

    public void setFirebaseId(String id) { firebaseId = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
