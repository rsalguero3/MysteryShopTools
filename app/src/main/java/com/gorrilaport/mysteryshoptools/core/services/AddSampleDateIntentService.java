package com.gorrilaport.mysteryshoptools.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.data.CategorySQLiteRepository;
import com.gorrilaport.mysteryshoptools.data.NoteSQLiteRepository;
import com.gorrilaport.mysteryshoptools.data.SampleData;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListActivity;

import java.util.ArrayList;
import java.util.List;


public class AddSampleDateIntentService extends IntentService {
    private final static String LOG_TAG = AddSampleDateIntentService.class.getSimpleName();


    public AddSampleDateIntentService() {
        super("AddSampleDateIntentService");

    }

    //Dummy implementation of the OnDatabaseOperation failed
    private OnDatabaseOperationCompleteListener mListener = new OnDatabaseOperationCompleteListener() {

        @Override
        public void onSaveOperationFailed(String error) {

        }

        @Override
        public void onSaveOperationSucceeded(long id) {

        }

        @Override
        public void onDeleteOperationCompleted(String message) {

        }

        @Override
        public void onDeleteOperationFailed(String error) {

        }

        @Override
        public void onUpdateOperationCompleted(String message) {

        }

        @Override
        public void onUpdateOperationFailed(String error) {

        }
    };



    @Override
    protected void onHandleIntent(Intent intent) {


        CategorySQLiteRepository categoryRepository = new CategorySQLiteRepository(getApplicationContext());
        NoteSQLiteRepository noteSQLiteRepository = new NoteSQLiteRepository(getApplicationContext());


        List<String> categories = new ArrayList<>();
        categories.add("Personal");
        categories.add("Work Related");
        categories.add("Family");
        categories.add("School Related");
        categories.add("Productivity");
        categories.add("General");
        categories.add("Social");


        for (String cat : categories) {
              categoryRepository.addAsync(cat, mListener);
        }

        //Get sample Notes
        List<Note> notes = SampleData.getSampleNotes();

        for (Note note : notes) {
            note.setCategoryId(categoryRepository.createOrGetCategoryId(note.getCategoryName()));
            noteSQLiteRepository.addAsync(note, mListener );

        }



        //When the data is added, restart the Notelist
        //Activity. The default data will be displayed
        Intent restartIntent = new Intent(this, NoteListActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(restartIntent);


    }


}
