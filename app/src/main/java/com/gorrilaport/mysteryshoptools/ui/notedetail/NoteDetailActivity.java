package com.gorrilaport.mysteryshoptools.ui.notedetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.Toast;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.OnEditNoteButtonClickedListener;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.addnote.AddNoteActivity;
import com.gorrilaport.mysteryshoptools.ui.addnote.NoteEditorFragment;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import javax.inject.Inject;

public class NoteDetailActivity extends AppCompatActivity {
    @Inject
    NoteListContract.Repository noteRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MysteryShopTools.getInstance().getAppComponent().inject(this);



        if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)){
            long noteId = getIntent().getLongExtra(Constants.NOTE_ID, 0);
            Note passedInNote = noteRepository.getNoteById(noteId);
            String title = passedInNote != null ? passedInNote.getTitle() : getString(R.string.note_detail);
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(noteId);
            fragment.setListener(new OnEditNoteButtonClickedListener() {
                @Override
                public void onEditNote(Note clickedNote) {
                    Intent editNoteIntent = new Intent(NoteDetailActivity.this, AddNoteActivity.class);
                    editNoteIntent.putExtra(Constants.NOTE_ID, clickedNote.getId());
                    startActivity(editNoteIntent);
                }
            });

            openFragment(fragment, title);
        }else {
            finish();
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


    public static Intent getStartIntent(final Context context, final long noteId) {
        Intent intent = new Intent(context, NoteDetailActivity.class);
        intent.putExtra(Constants.NOTE_ID, noteId);
        return intent;
    }

    @Override
    public void onBackPressed(){
        Toast toast = Toast.makeText(getApplicationContext(), "Saving",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
        toast.show();
        super.onBackPressed();
        finish();
    }
}
