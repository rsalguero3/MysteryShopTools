package com.gorrilaport.mysteryshoptools.core.dagger;

import android.content.Context;

import com.gorrilaport.mysteryshoptools.data.CategorySQLiteRepository;
import com.gorrilaport.mysteryshoptools.data.FireBaseRepository;
import com.gorrilaport.mysteryshoptools.data.NoteSQLiteRepository;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryListContract;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PersistenceModule {
    @Provides
    @Singleton
    public NoteListContract.Repository providesNoteRepository(Context context){
        return new NoteSQLiteRepository(context);
    }

    @Provides
    @Singleton
    public CategoryListContract.Repository providesCategoryManager(Context context){
        return new CategorySQLiteRepository(context);
    }

    @Provides
    @Singleton
    public NoteListContract.FireBaseRepository providesFireBaseRepository(Context context){
        return new FireBaseRepository();
    }
}
