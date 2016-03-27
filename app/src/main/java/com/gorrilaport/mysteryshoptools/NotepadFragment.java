package com.gorrilaport.mysteryshoptools;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ricardo on 3/6/2016.
 */
public class NotepadFragment extends SingleFragment {
    private FloatingActionButton mAddNoteAction;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteAdapter mAdapter;

    public static final int REQUEST_CODE_NOTEPAD_FRAG = 0;
    public Activity mActivity;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (Activity)context;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //Direct floating action button to new Activity
        mAddNoteAction = (FloatingActionButton)getActivity().findViewById(R.id.fab_notepad);
        mAddNoteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNotes.class);
                startActivityForResult(intent, REQUEST_CODE_NOTEPAD_FRAG, ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
            }
        });
        //Database initialization
        mDatabase = new NotePadBaseHelper(getContext().getApplicationContext()).getWritableDatabase();
        List<Notes> nNotesList = new ArrayList<>();

        //Initialize recyclerView, setting adapter
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.fragment_recyclerView);
        mAdapter = new NoteAdapter(nNotesList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    //Get results back from AddNotes Class
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        ArrayList<String> extrasArray = data.getStringArrayListExtra("add_notes_extras");
        if(data.getStringExtra("title_input").length() == 0){
            //do nothing if both the title and the following views are empty.
            if(extrasArray.isEmpty()){

            }
            else {
                int num = extrasArray.size();
                for (int i = 0; i < num; i++);

            }

        }
    }

// Implementation of a recyclerView Adapter holding Notes objects
    private class NoteListHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mTextInput;
        public CardView mCardView;

        public NoteListHolder(View itemView){
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.cv);
            mTitle = (TextView)itemView.findViewById(R.id.title);
            mTextInput = (TextView) itemView.findViewById(R.id.text_input);
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteListHolder>{

        private List<Notes> mNotes;

        public NoteAdapter(List<Notes> notes){
            mNotes = notes;
        }

        @Override
        public NoteListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
            View view = layoutinflater.inflate(R.layout.notes_list_view, parent, false);
            return new NoteListHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteListHolder holder, int position) {
            Notes notes = mNotes.get(position);
            holder.mTitle.setText(notes.getTitle());
            holder.mTextInput.setText(notes.getTextInput());
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }
    }


}


