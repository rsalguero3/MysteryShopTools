package com.gorrilaport.mysteryshoptools.data;

import android.content.res.Resources;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.thefinestartist.utils.content.ResourcesUtil.getResourceName;
import static com.thefinestartist.utils.content.ResourcesUtil.getString;

public class SampleData{


    public static List<Note> getSampleNotes(){

        List<Note> notes = new ArrayList<>();
        //create the dummy note
        Note note1 = new Note();
        note1.setTitle(Resources.getSystem().getString(R.string.SampleCategory1));
        note1.setContent(getString(R.string.SampleContent1));
        Calendar calendar1 = GregorianCalendar.getInstance();
        note1.setDateModified(calendar1.getTimeInMillis());
        note1.setNoteType(Constants.NOTE_TYPE_AUDIO);
        notes.add(note1);

        //create the dummy note
        Note note2 = new Note();
        note2.setTitle(getString(R.string.SampleTitle2));
        note2.setContent(getString(R.string.SampleContent2));

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
}
