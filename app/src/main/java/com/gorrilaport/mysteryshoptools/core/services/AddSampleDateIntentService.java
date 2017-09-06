package com.gorrilaport.mysteryshoptools.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.listeners.OnDatabaseOperationCompleteListener;
import com.gorrilaport.mysteryshoptools.data.CategorySQLiteRepository;
import com.gorrilaport.mysteryshoptools.data.NoteSQLiteRepository;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.thefinestartist.utils.content.ResourcesUtil.getString;


public class AddSampleDateIntentService extends IntentService {
    private final static String LOG_TAG = AddSampleDateIntentService.class.getSimpleName();


    public AddSampleDateIntentService() {
        super("AddSampleDateIntentService");

    }

    public List<Note> getSampleNotes(){

        List<Note> notes = new ArrayList<>();
        //create the dummy note
        Note note1 = new Note();
        note1.setTitle(getString(R.string.SampleTitle1));
        note1.setContent(getString(R.string.SampleContent1));
        note1.setCategoryName(getString(R.string.SampleCategory3));
        Calendar calendar1 = GregorianCalendar.getInstance();
        note1.setDateModified(calendar1.getTimeInMillis());
        note1.setNoteType(Constants.NOTE_TYPE_AUDIO);
        notes.add(note1);

        //create the dummy note
        Note note2 = new Note();
        note2.setTitle(getString(R.string.SampleTitle2));
        note2.setContent(getString(R.string.SampleContent2));
        note2.setCategoryName(getString(R.string.SampleCategory4));

        //change the date to random time
        Calendar calendar2 = GregorianCalendar.getInstance();
        calendar2.add(Calendar.DAY_OF_WEEK, -1);
        calendar2.add(Calendar.MILLISECOND, 10005623);
        note2.setDateModified(calendar2.getTimeInMillis());
        note2.setNoteType(Constants.NOTE_TYPE_IMAGE);
        notes.add(note2);


        //create the dummy note
        Note note3 = new Note();
        note3.setTitle(getString(R.string.SampleTitle3));
        note3.setContent(getString(R.string.SampleContent3));
        note3.setCategoryName(getString(R.string.SampleCategory3));

        //change the date to random time
        Calendar calendar3 = GregorianCalendar.getInstance();
        calendar3.add(Calendar.DAY_OF_WEEK, -2);
        calendar3.add(Calendar.MILLISECOND, 8962422);
        note3.setDateModified(calendar3.getTimeInMillis());
        note3.setNoteType(Constants.NOTE_TYPE_REMINDER);
        notes.add(note3);


        //create the dummy note
        Note note4 = new Note();
        note4.setTitle(getString(R.string.SampleTitle4));
        note4.setContent(getString(R.string.SampleContent4));
        note4.setCategoryName(getString(R.string.SampleCategory3));

        //pad the date with random number of days and minute
        //so all the notes do not have the same time stamp
        Calendar calendar4 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.DAY_OF_WEEK, -4);
        calendar4.add(Calendar.MILLISECOND, 49762311);
        note4.setDateModified(calendar4.getTimeInMillis());
        note4.setNoteType(Constants.NOTE_TYPE_TEXT);
        notes.add(note4);


        //create the dummy note
        Note note5 = new Note();
        note5.setTitle(getString(R.string.SampleTitle5));
        note5.setContent(getString(R.string.SampleContent5));
        note5.setCategoryName(getString(R.string.SampleCategory3));

        //pad the date with two days
        //pad the date with random number of days and minute
        //so all the notes do not have the same time stamp
        Calendar calendar5 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.MONTH, -2);
        calendar5.add(Calendar.MILLISECOND, 2351689);
        note5.setDateModified(calendar5.getTimeInMillis());
        note5.setNoteType(Constants.NOTE_TYPE_AUDIO);
        notes.add(note5);

        return notes;
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
        categories.add(getString(R.string.SampleCategory1));
        categories.add(getString(R.string.SampleCategory2));
        categories.add(getString(R.string.SampleCategory3));
        categories.add(getString(R.string.SampleCategory4));
        categories.add(getString(R.string.SampleCategory5));
        categories.add(getString(R.string.SampleCategory6));
        categories.add(getString(R.string.SampleCategory7));



        for (String cat : categories) {
              categoryRepository.addAsync(cat, mListener);
        }

        //Get sample Notes
        List<Note> notes = getSampleNotes();

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
