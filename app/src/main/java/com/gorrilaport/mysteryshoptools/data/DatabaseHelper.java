package com.gorrilaport.mysteryshoptools.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import com.gorrilaport.mysteryshoptools.util.Constants;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper mDatabaseInstance = null;
    private Context mContext;

    public static DatabaseHelper newInstance(Context context) {
        //first check to see if the database helper
        //member data is null
        //create a new one if it is null

        if (mDatabaseInstance == null) {
            mDatabaseInstance = new DatabaseHelper(context.getApplicationContext());
        }

        //either way we have to always return an instance of
        //our database class each time this method is called
        return mDatabaseInstance;
    }

    //make the constructor private so it cannot be
    //instantiated outside of this class
    private DatabaseHelper(Context context) {
        super(context, Constants.SQLITE_DATABASE, null, DATABASE_VERSION);
        this.mContext = context;

    }

    public void deleteDatabase(){
        mContext.deleteDatabase(Constants.SQLITE_DATABASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + Constants.CATEGORY_TABLE + " ADD COLUMN "
                    + Constants.COLUMN_COLOR + " INTEGER DEFAULT -1"
            );
            db.execSQL("ALTER TABLE " + Constants.NOTES_TABLE + " ADD COLUMN "
                    + Constants.COLUMN_FIREBASE_ID+ " TEXT DEFAULT NULL"

            );
            db.execSQL("ALTER TABLE " + Constants.NOTES_TABLE + " ADD COLUMN "
                    + Constants.COLUMN_COLOR + " INTEGER DEFAULT -1"
            );
        }
    }


    private static final String CREATE_TABLE_NOTE = "create table "
            + Constants.NOTES_TABLE
            + "("
            + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Constants.COLUMN_FIREBASE_ID + " TEXT DEFAULT NULL, "
            + Constants.COLUMN_TITLE + " TEXT NOT NULL, "
            + Constants.COLUMN_CONTENT + " TEXT NOT NULL, "
            + Constants.COLUMN_NEXT_REMINDER + " INTEGER, "
            + Constants.COLUMN_LOCAL_AUDIO_PATH + " TEXT, "
            + Constants.COLUMN_LOCAL_SKETCH_PATH + " TEXT, "
            + Constants.COLUMNS_CATEGORY_ID + " INTEGER,"
            + Constants.COLUMN_COLOR + " INTEGER DEFAULT -1, "
            + Constants.COLUMN_CATEGORY_NAME + " TEXT, "
            + Constants.COLUMN_MODIFIED_TIME + " BIGINT NOT NULL, "
            + Constants.COLUMN_CREATED_TIME + " BIGINT NOT NULL, "
            + Constants.COLUMNS_NOTE_TYPE + " TEXT, "
            + "FOREIGN KEY(category_id) REFERENCES category(_id)" + ")";

    //String to create a category table
    private static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + Constants.CATEGORY_TABLE + "("
                    + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Constants.COLUMN_TITLE + " TEXT NOT NULL, "
                    + Constants.COLUMN_CREATED_TIME + " BIGINT, "
                    + Constants.COLUMN_MODIFIED_TIME + " BIGINT, "
                    + Constants.COLUMN_COLOR + " INTEGER DEFAULT -1" + ")";

    private static final String CREATE_IMAGE_TABLE =
            "CREATE TABLE " + Constants.IMAGE_TABLE + "("
                    + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Constants.COLUMN_IMAGE_PATH + " TEXT NOT NULL,"
                    + Constants.COLUMN_NOTE_ID + " INTEGER,"
                    + " FOREIGN KEY(note_id) REFERENCES note(_id))";
}
