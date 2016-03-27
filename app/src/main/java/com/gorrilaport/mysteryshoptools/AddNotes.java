package com.gorrilaport.mysteryshoptools;

import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import java.util.ArrayList;

public class AddNotes extends AppCompatActivity {
    private Toolbar mToolbar;
    private int layoutIndexCount = 2;
    private int editTextId = 0;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditTextNoteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        setupWindowAnimations();
        mToolbar = (Toolbar)findViewById(R.id.app_bar_add_notes);
        setSupportActionBar(mToolbar);


        mRecyclerView = (RecyclerView)findViewById(R.id.activity_add_notes_recyclerView);
        mLayoutManager = new LinearLayoutManager(this);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EditTextNoteAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.add_notes_action_button){
            mAdapter.numOfView += 1;
            mAdapter.notifyItemInserted(mAdapter.numOfView - 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent data = new Intent();
        int numOfViews = mLayoutManager.getChildCount();
        View title = mRecyclerView.getChildAt(0);
        EditText editTitle = (EditText)title.findViewById(R.id.add_note_edit_text_title);
        String textTitle = editTitle.getText().toString();
        data.putExtra("title_input", textTitle);
        ArrayList<String> extrasList = new ArrayList<>();
        for (int i = 1; i < numOfViews; i++){
            View v = mRecyclerView.getChildAt(i);
            EditText editText = (EditText)v.findViewById(R.id.create_new_edit_text);
            String text = editText.getText().toString();
            extrasList.add(text);
        }
        data.putStringArrayListExtra("add_notes_extras", extrasList);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
    @TargetApi(21)
    private void setupWindowAnimations(){
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        public EditText mEditText, EditTextTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mEditText = (EditText) itemView.findViewById(R.id.create_new_edit_text);
            EditTextTitle = (EditText) itemView.findViewById(R.id.add_note_edit_text_title);
        }

    }

        private class EditTextNoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            //default view will add 2 viewHolders one with type TYPE_TITLE other TYPE_DEFAULT
            public int numOfView = 2;
            private static final int TYPE_TITLE = 0;
            private static final int TYPE_DEFAULT = 1;

            public EditTextNoteAdapter() {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = getLayoutInflater();
                if (viewType == TYPE_TITLE) {
                    View v = inflater.inflate(R.layout.edit_title_add_notes, parent, false);
                    return new ViewHolder(v);
                } else {
                    View v = inflater.inflate(R.layout.edit_text_add_notes, parent, false);
                    return new ViewHolder(v);
                }
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                }

            @Override
            public int getItemCount() {
                return numOfView;
            }

            @Override
            public int getItemViewType(int position) {
                if (position == 0) {
                    return TYPE_TITLE;
                } else {
                    return TYPE_DEFAULT;
                }
            }
        }
}
