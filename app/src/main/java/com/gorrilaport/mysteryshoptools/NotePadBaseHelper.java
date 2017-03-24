package com.gorrilaport.mysteryshoptools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gorrilaport.mysteryshoptools.NotePadDbSchema.NotesTable;


/**
 * Created by Ricardo on 3/13/2016.
 */
public class NotePadBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "notePadBase.db";

    public NotePadBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NotesTable.NAME + " (" +
                NotesTable.Cols.NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesTable.Cols.TITLE + " TEXT, " +
                NotesTable.Cols.TEXT + " TEXT, " +
                NotesTable.Cols.LIST_PARENT + " INTEGER, " +
                NotesTable.Cols.IS_CHECKED + " INTEGER, " +
                NotesTable.Cols.TIME_CREATED + " TEXT, " +
                NotesTable.Cols.COLOR + " INTEGER, " +
                NotesTable.Cols.TYPE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
