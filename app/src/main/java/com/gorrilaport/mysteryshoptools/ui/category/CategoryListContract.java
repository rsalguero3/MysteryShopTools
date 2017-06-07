package com.gorrilaport.mysteryshoptools.ui.category;


import android.content.Context;

import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Category;

import java.util.List;
import java.util.Map;

public interface CategoryListContract {
    interface View{
        void showAddNewCategoryDialog();
        void showCategory(String category);
        void showEmptyText();
        void hideEmptyText();
        void displayMessage(String message);
        void showCategories(List<Category> categories, Map<Long, Integer> noteCount);
        void showConfirmDeleteCategoryPrompt(Category category);
        void showEditCategoryForm(Category category);
        Context getContext();
    }


    interface Action{
        void onSelectCategory();
        void addNewCategory(Category category);
        void loadCategories();
        void onDeleteCategoryButtonClicked(Category category);
        void onEditCategoryButtonClicked(Category category);
        void deleteCategory(Category category);
    }


    interface Repository {
        void addAsync(String name, OnDatabaseOperationCompleteListener listener);
        void updateAsync(Category category, OnDatabaseOperationCompleteListener listener);
        void deleteAsync(Category category, OnDatabaseOperationCompleteListener listener);
        List<Category> getAllCategories(String sortOption, boolean sortOrder);
        int getNoteCount(long categoryId);
        long createOrGetCategoryId(String categoryName);
        void removeAll();
        Category getCategoryById(long id);
    }

}
