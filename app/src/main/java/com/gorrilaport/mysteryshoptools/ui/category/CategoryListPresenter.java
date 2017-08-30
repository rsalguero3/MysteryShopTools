package com.gorrilaport.mysteryshoptools.ui.category;

import android.content.SharedPreferences;
import android.util.Log;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.model.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class CategoryListPresenter implements
        CategoryListContract.Action, OnDatabaseOperationCompleteListener {

    @Inject CategoryListContract.Repository mRepository;
    @Inject
    SharedPreferences mSharedPreference;

    private final CategoryListContract.View mView;

    private final static String LOG_CAT = CategoryListPresenter.class.getSimpleName();
    private final static boolean DEBUG = true;


    public CategoryListPresenter(CategoryListContract.View categoryView) {
        MysteryShopTools.getInstance().getAppComponent().inject(this);
        mView = categoryView;

    }

    @Override
    public void onSelectCategory() {
        String sortColumn = mSharedPreference.getString("sort_options", mView.getContext().getString(R.string.column_title));
        boolean sortOrder = mSharedPreference.getBoolean("list_sort_order", true);


        Map<Long, Integer> noteCount = new HashMap<Long, Integer>();
        for (Category category : getCategories(sortColumn, sortOrder)){
            noteCount.put(category.getId(), mRepository.getNoteCount(category.getId()));
        }

    }

    @Override
    public void addNewCategory(Category category) {
        mView.showCategory(category.getTitle());

        if (category.getId() > 0){
            mRepository.updateAsync(category, this);
        }else {
            mRepository.addAsync(category.getTitle(), this);
        }

    }

    @Override
    public void loadCategories() {

        String sortColumn = mSharedPreference.getString("sort_options", mView.getContext().getString(R.string.column_title));
        String sordOrderValue = mSharedPreference.getString("list_sort_order", "true");
        boolean sortOrder = Boolean.valueOf(sordOrderValue);

        List<Category> mCategories = getCategories(sortColumn, sortOrder);
        if (mCategories != null && mCategories.size() > 0){
            mView.hideEmptyText();

            Map<Long, Integer> noteCount = new HashMap<Long, Integer>();

            try {
                for (Category category: mCategories){
                    if (DEBUG){
                        Log.d(LOG_CAT, category.getTitle() + ": " + category.getId());
                    }
                    noteCount.put(category.getId(), mRepository.getNoteCount(category.getId()));
                    if (DEBUG){
                        Log.d(LOG_CAT, noteCount.get(category.getId()) + ": " + category.getId());
                    }
                }
            } catch (Exception e) {
                mView.displayMessage("Error: " + e.getMessage());
            }
            mView.showCategories(mCategories, noteCount);

        } else {
            mView.showEmptyText();
        }
    }

    @Override
    public void onDeleteCategoryButtonClicked(Category category) {
        mView.showConfirmDeleteCategoryPrompt(category);

    }

    @Override
    public void onEditCategoryButtonClicked(Category category) {
        mView.showEditCategoryForm(category);
    }

    @Override
    public void deleteCategory(Category category) {
        mRepository.deleteAsync(category, this);
    }




    public List<Category> getCategories(String sortOption, boolean sortOrder) {
        return mRepository.getAllCategories(sortOption, sortOrder);
    }


    @Override
    public void onSaveOperationFailed(String error) {
        mView.displayMessage(error);
    }

    @Override
    public void onSaveOperationSucceeded(long id) {
        mView.displayMessage("Saved");
        loadCategories();
    }

    @Override
    public void onDeleteOperationCompleted(String message) {
        mView.displayMessage(message);
        loadCategories();
    }

    @Override
    public void onDeleteOperationFailed(String error) {
        mView.displayMessage(error);
    }

    @Override
    public void onUpdateOperationCompleted(String message) {
        mView.displayMessage(message);
        loadCategories();
    }

    @Override
    public void onUpdateOperationFailed(String error) {
        mView.displayMessage(error);
    }
}
