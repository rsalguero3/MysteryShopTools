package com.gorrilaport.mysteryshoptools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gorrilaport.mysteryshoptools.NotePadDbSchema.NotesTable;
import com.gorrilaport.mysteryshoptools.NotePadDbSchema.TitleTable;


/**
 * Created by Ricardo on 3/13/2016.
 */
public class NotePadBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "notePadBase.db";

    public static final String[] NOTES_ALL_COLUMNS = {NotesTable.Cols.NOTE_ID,
            NotesTable.Cols.UUID, NotesTable.Cols.TEXT, NotesTable.Cols.LIST_PARENT,
            NotesTable.Cols.IS_CHECKED, NotesTable.Cols.TIME_CREATED, NotesTable.Cols.TITLE_ID};

    public static final String[] TITLE_ALL_COLUMNS = {TitleTable.Cols.TITLE_ID, TitleTable.Cols.TITLE,
            TitleTable.Cols.COLOR, TitleTable.Cols.TYPE};

    public NotePadBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TitleTable.NAME + " (" +
                TitleTable.Cols.TITLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TitleTable.Cols.TITLE + " TEXT, " +
                TitleTable.Cols.COLOR + " INTEGER, " +
                TitleTable.Cols.TYPE + " INTEGER);");
        db.execSQL("CREATE TABLE " + NotesTable.NAME + " (" +
                NotesTable.Cols.NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesTable.Cols.UUID + " VARCHAR(255), " +
                NotesTable.Cols.TEXT + " TEXT, " +
                NotesTable.Cols.LIST_PARENT + " INTEGER, " +
                NotesTable.Cols.IS_CHECKED + " INTEGER, " +
                NotesTable.Cols.TIME_CREATED + " TEXT, " +
                NotesTable.Cols.TITLE_ID + " INTEGER, " +
                "FOREIGN KEY(" + NotesTable.Cols.TITLE_ID + ") REFERENCES title(" + TitleTable.Cols.TITLE_ID + "));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
