package com.gorrilaport.mysteryshoptools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class AddNotes extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mEditTextTitle, mEditTextNote;
    private Button mButtonAddNote;
    private int layoutIndexCount = 2;
    private int editTextId = 0;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        mToolbar = (Toolbar)findViewById(R.id.app_bar_add_notes);
        setSupportActionBar(mToolbar);

        mEditTextTitle = (EditText)findViewById(R.id.add_note_edit_text_title);
        mEditTextNote = (EditText)findViewById(R.id.add_note_edit_text_note);
        mButtonAddNote = (Button)findViewById(R.id.add_note_more_button);

        mButtonAddNote.setOnClickListener(new View.OnClickListener() {
            /*layoutIndexCount makes sures that the first edit text is added after the second child view and
            other views created are appended to the previous created */
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout_add_notes);
                layoutIndexCount = layoutIndexCount + 1;
                linearLayout.addView(createEditText(), layoutIndexCount);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_notes, menu);
        return true;
    }
//Create a new EditText field and set it an id of 1, after it will be 1 + n times
    public EditText createEditText(){
        EditText editText = new EditText(this);
        editTextId =+ 1;
        editText.setId(editTextId);
        editText.setHint(R.string.add_notes_note_hint);
        editText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        return editText;
    }
    private class EditTextViewHolder extends RecyclerView.ViewHolder{
        private EditText mEditText;

        public EditTextViewHolder(View itemView){
            super(itemView);
            mEditText = (EditText)findViewById(R.id.create_new_edit_text);
        }
    }
    private class EditTextNoteAdapter extends RecyclerView.Adapter<EditTextViewHolder>{
        private List<EditText> editTextList = new ArrayList<>();
        public EditTextNoteAdapter(List<EditText> list){
            editTextList = list;
        }

        @Override
        public EditTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(EditTextViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
