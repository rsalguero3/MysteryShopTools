package com.gorrilaport.mysteryshoptools.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategorySQLiteRepository implements CategoryListContract.Repository {

    private final DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase database;

    public CategorySQLiteRepository(Context context) {
        mDatabaseHelper = DatabaseHelper.newInstance(context);
        database = mDatabaseHelper.getWritableDatabase();
    }


    @Override
    public void addAsync(String name, OnDatabaseOperationCompleteListener listener) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, name);
        values.put(Constants.COLUMN_CREATED_TIME, System.currentTimeMillis());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        try {
            long result = database.insertOrThrow(Constants.CATEGORY_TABLE, null, values);
            listener.onSaveOperationSucceeded(result);
        } catch (SQLException e) {
            listener.onSaveOperationFailed(e.getLocalizedMessage());

        }
    }

    @Override
    public void updateAsync(Category category, OnDatabaseOperationCompleteListener listener) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, category.getTitle());
        values.put(Constants.COLUMN_CREATED_TIME, System.currentTimeMillis());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());

        try {
            long result = database.insertOrThrow(Constants.CATEGORY_TABLE, null, values);
            listener.onSaveOperationSucceeded(result);
        } catch (SQLiteException e){
            listener.onSaveOperationFailed(e.getLocalizedMessage());
        }

    }

    @Override
    public void deleteAsync(Category category, OnDatabaseOperationCompleteListener listener) {
        // Ensure database exists.
        if (database != null) {
            int result = database.delete(Constants.CATEGORY_TABLE, Constants.COLUMN_ID + " = " + category.getId(), null);

            if (result > 0) {
                listener.onDeleteOperationCompleted("Deleted");
            } else {
                listener.onDeleteOperationCompleted("Unable to Delete Category");
            }

        }

    }

    @Override
    public List<Category> getAllCategories(String sortOption, boolean sortOrder) {
        List<Category> categories = new ArrayList<Category>();

        //Command to select all Categories
        String selectQuery = "SELECT * FROM " + Constants.CATEGORY_TABLE;

        //Get a cursor for all Categories in the database
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                categories.add(Category.fromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return categories;
    }

    @Override
    public int getNoteCount(long categoryId) {
        String rawQuery = "SELECT * FROM " + Constants.NOTES_TABLE + " WHERE  " + Constants.COLUMNS_CATEGORY_ID + " = " + categoryId;
        Cursor cursor = database.rawQuery(rawQuery, null);
        if (cursor != null){
            return cursor.getCount();
        }

        return 0;
    }

    @Override
    public long createOrGetCategoryId(String categoryName) {
        Category foundCategory = getCategory(categoryName);
        if(foundCategory == null) {
            foundCategory = addCategory(categoryName);
        }
        return foundCategory.getId();
    }

    private Category addCategory(final String categoryName) {
        Category category = new Category();
        category.setTitle(categoryName);
        saveCategory(category);
        return category;
    }

    private void saveCategory(Category category) {

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NAME, category.getTitle());
        try {
            database.insertOrThrow(Constants.CATEGORY_TABLE, null, values);
        } catch (SQLException e) {


        }

    }

    private Category getCategory(final String categoryName) {

        Category category = null;
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.CATEGORY_TABLE + " " +
                "WHERE " + Constants.COLUMN_TITLE + " = '" + categoryName + "'", null);
        if (cursor.moveToFirst()){
            category = Category.fromCursor(cursor);
        }
        cursor.close();
        return category;
    }

    @Override
    public void removeAll() {

    }

    @Override
    public Category getCategoryById(long id) {
        //Get the cursor representing the Category
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.CATEGORY_TABLE + " WHERE " +
                Constants.COLUMN_ID + " = '" + id + "'", null);

        //Create a variable of data type Note
        Category category;
        if (cursor.moveToFirst()){
            category = Category.fromCursor(cursor);
        }else {
            category = null;
        }
        cursor.close();
        //Return result: either a valid category or null
        return  category;
    }
}

