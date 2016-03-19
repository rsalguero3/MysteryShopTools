package com.gorrilaport.mysteryshoptools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.gorrilaport.mysteryshoptools.NotePadDbSchema.NotesTable;

/**
 * Created by Ricardo on 3/13/2016.
 */
public class NotePadBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "notePadBase.db";

    public NotePadBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + NotesTable.NAME + "(" + " _id integer primary key autoincrement, " + NotesTable.Cols.UUID + ", " +
                NotesTable.Cols.TITLE + ", " +
                NotesTable.Cols.DATE + ", " +
                NotesTable.Cols.TEXTINPUT + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
