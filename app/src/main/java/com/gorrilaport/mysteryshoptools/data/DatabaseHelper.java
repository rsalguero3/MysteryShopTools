package com.gorrilaport.mysteryshoptools.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gorrilaport.mysteryshoptools.util.Constants;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    public static final String LOG_CAT = DatabaseHelper.class.getSimpleName();

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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    private static final String CREATE_TABLE_NOTE = "create table "
            + Constants.NOTES_TABLE
            + "("
            + Constants.COLUMN_ID + " integer primary key autoincrement, "
            + Constants.COLUMN_TITLE + " text not null, "
            + Constants.COLUMN_CONTENT + " text not null, "
            + Constants.COLUMN_NEXT_REMINDER + " integer, "
            + Constants.COLUMN_LOCAL_AUDIO_PATH + " text, "
            + Constants.COLUMN_LOCAL_IMAGE_PATH + " text, "
            + Constants.COLUMN_LOCAL_SKETCH_PATH + " text, "
            + Constants.COLUMNS_CATEGORY_ID + " integer,"
            + Constants.COLUMN_CATEGORY_NAME + " text, "
            + Constants.COLUMN_MODIFIED_TIME + " BIGINT not null, "
            + Constants.COLUMN_CREATED_TIME + " BIGINT not null, "
            + Constants.COLUMNS_NOTE_TYPE + " text, "
            + "FOREIGN KEY(category_id) REFERENCES category(_id)" + ")";

    //String to create a category table
    private static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + Constants.CATEGORY_TABLE + "("
                    + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Constants.COLUMN_TITLE + " TEXT NOT NULL, "
                    + Constants.COLUMN_CREATED_TIME + " BIGINT, "
                    + Constants.COLUMN_MODIFIED_TIME + " BIGINT " + ")";
}
