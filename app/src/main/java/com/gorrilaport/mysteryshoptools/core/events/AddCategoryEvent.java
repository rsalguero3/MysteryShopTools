package com.gorrilaport.mysteryshoptools.core.events;


import com.gorrilaport.mysteryshoptools.model.Category;

public class AddCategoryEvent{
    private final Category mCategory;

    public AddCategoryEvent(Category category) {
        mCategory = category;
    }

    public Category getCategory() {
        return mCategory;
    }
}
