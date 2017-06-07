package com.gorrilaport.mysteryshoptools.ui.addnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import javax.inject.Inject;

public class AddNoteActivity extends AppCompatActivity {

    @Inject
    NoteListContract.Repository noteRepository;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_goleft);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MysteryShopTools.getInstance().getAppComponent().inject(this);

        if (getIntent().hasExtra(Constants.NOTE_ID)){
            long noteId = getIntent().getLongExtra(Constants.NOTE_ID, 0);
            String screenTitle = noteRepository.getNoteById(noteId).getTitle();
            openFragment(NoteEditorFragment.newInstance(noteId), screenTitle);
        }else {
            openFragment(NoteEditorFragment.newInstance(0), "Note Editor");
        }


    }


    private void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }

}
