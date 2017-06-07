package com.gorrilaport.mysteryshoptools.core.listeners;


import com.gorrilaport.mysteryshoptools.model.Category;

public interface OnCategorySelectedListener {
    void onCategorySelected(Category selectedCategory);
    void onEditCategoryButtonClicked(Category selectedCategory);
    void onDeleteCategoryButtonClicked(Category selectedCategory);
}
