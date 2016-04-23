package com.gorrilaport.mysteryshoptools;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Ricardo on 4/5/2016.
 */
public class NoteProvider extends ContentProvider {
    private static final String AUTHORITY = "com.gorillaport.mysteryshoptools.noteprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;
    private static final int TITLE = 3;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
        uriMatcher.addURI(AUTHORITY, "/title", TITLE);
    }
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        NotePadBaseHelper helper = new NotePadBaseHelper(getContext().getApplicationContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int mUri = uriMatcher.match(uri);
        Cursor cursor = null;
        switch (mUri){
            case NOTES:
                cursor = database.query(NotePadDbSchema.NotesTable.NAME, NotePadBaseHelper.NOTES_ALL_COLUMNS,
                        selection, null, null, null, NotePadDbSchema.NotesTable.Cols.TIME_CREATED + " DESC");
                Log.d("cursor", "NOTES CALLED");
                break;
            case NOTES_ID:
                break;
            case TITLE:
                cursor = database.query(NotePadDbSchema.TitleTable.NAME, NotePadBaseHelper.TITLE_ALL_COLUMNS,
                        selection, null, null, null, NotePadDbSchema.TitleTable.Cols.TITLE_ID);
                Log.d("cursor", "TITLE CALLED");
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int mUri = uriMatcher.match(uri);
        Uri uri1;
        long id;
        switch (mUri){
            case NOTES:
                id = database.insert(NotePadDbSchema.NotesTable.NAME, null, values);
                uri1 = Uri.parse(BASE_PATH + "/" + id);
                break;
            case TITLE:
                id = database.insert(NotePadDbSchema.TitleTable.NAME, null, values);
                uri1 = Uri.parse(BASE_PATH + "/" + id);
                break;
            default:
                uri1 = Uri.parse(BASE_PATH);
        }
        return uri1;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(NotePadDbSchema.NotesTable.NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(NotePadDbSchema.NotesTable.NAME, values, selection, selectionArgs);
    }
}
