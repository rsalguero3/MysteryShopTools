package com.gorrilaport.mysteryshoptools.core.dagger;

import com.gorrilaport.mysteryshoptools.ui.addnote.AddNoteActivity;
import com.gorrilaport.mysteryshoptools.ui.addnote.AddNotePresenter;
import com.gorrilaport.mysteryshoptools.ui.addnote.NoteEditorFragment;
import com.gorrilaport.mysteryshoptools.ui.category.AddEditCategoryDialogFragment;
import com.gorrilaport.mysteryshoptools.ui.category.SelectCategoryDialogFragment;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryListPresenter;
import com.gorrilaport.mysteryshoptools.ui.notedetail.NoteDetailActivity;
import com.gorrilaport.mysteryshoptools.ui.notedetail.NoteDetailPresenter;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListFragment;
import com.gorrilaport.mysteryshoptools.ui.notelist.NotesListPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                BusModule.class,
                PersistenceModule.class
        }
)

public interface AppComponent {
        void inject(AddEditCategoryDialogFragment fragment);
        void inject(CategoryListPresenter presenter);
        void inject(NoteListFragment fragment);
        void inject(NotesListPresenter presenter);
        void inject(AddNotePresenter presenter);
        void inject(NoteDetailPresenter presenter);
        void inject(NoteDetailActivity activity);
        void inject(AddNoteActivity activity);
        void inject(SelectCategoryDialogFragment fragment);
        void inject(NoteEditorFragment fragment);


}
