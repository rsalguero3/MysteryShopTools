package com.gorrilaport.mysteryshoptools;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
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
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
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

    public Cursor titleCursor;
    public Cursor textCursor;

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivityForResult(intent, REQUEST_CODE_NOTEPAD_FRAG,
                            ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
                }
                else {
                    startActivityForResult(intent, REQUEST_CODE_NOTEPAD_FRAG);
                }
            }
        });
        //Database query initialization
        textCursor = getContext().getContentResolver().query(Uri.parse(NoteProvider.CONTENT_URI + "notes"),
                NotePadBaseHelper.NOTES_ALL_COLUMNS, null, null, null, null);
        titleCursor = getContext().getContentResolver().query(
                Uri.parse(NoteProvider.CONTENT_URI + "title"),
                NotePadBaseHelper.TITLE_ALL_COLUMNS, null, null, null, null);


        //Initialize recyclerView, setting adapter
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.fragment_recyclerView);
        mAdapter = new NoteAdapter(getContext(), textCursor);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    //Get results back from AddNotes Class
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        ArrayList<String> extrasArray = data.getStringArrayListExtra("add_notes_extras");
        String text;
        Log.d("activity called", "yes");
        if(data.getStringExtra("title_input").length() == 0){
            //do nothing if both the title and the following views are empty.
            if(extrasArray.isEmpty()){

            }
            else {
                ContentValues values1 = new ContentValues();
                values1.put(NotePadDbSchema.TitleTable.Cols.TITLE, "");
                getContext().getContentResolver().insert(Uri.parse(NoteProvider.CONTENT_URI + "title"), values1);
                Log.d("values1 = ", Uri.parse(NoteProvider.CONTENT_URI + "title").toString());

                StringBuilder builder = new StringBuilder();
                for (String s : extrasArray){
                    builder.append(s);
                }

                ContentValues values = new ContentValues();
                values.put(NotePadDbSchema.NotesTable.Cols.TEXT, builder.toString());
                getContext().getContentResolver().insert(Uri.parse(NoteProvider.CONTENT_URI + "notes"), values);
                Log.d("values = ", NoteProvider.CONTENT_URI.toString());

                textCursor = getContext().getContentResolver().query(Uri.parse(NoteProvider.CONTENT_URI + "notes"),
                        NotePadBaseHelper.NOTES_ALL_COLUMNS, null, null, null, null);
                titleCursor = getContext().getContentResolver().query(
                        Uri.parse(NoteProvider.CONTENT_URI + "title"),
                        NotePadBaseHelper.TITLE_ALL_COLUMNS, null, null, null, null);
                //mAdapter.changeCursor(titleCursor);
            }
        }
    }

    public class NoteAdapter extends CursorRecyclerViewAdapter<NoteAdapter.NoteListHolder>{

        public NoteAdapter(Context context, Cursor cursor){
            super(context, cursor);
        }

        // Implementation of a recyclerView Adapter holding Notes objects
        public class NoteListHolder extends RecyclerView.ViewHolder{
            public TextView mTitle, mTextInput;
            public CardView mCardView;

            public NoteListHolder(View itemView){
                super(itemView);
                mCardView = (CardView)itemView.findViewById(R.id.cv);
                mTitle = (TextView)itemView.findViewById(R.id.title);
                mTextInput = (TextView) itemView.findViewById(R.id.text_input);
            }
        }

        @Override
        public NoteListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
            View view = layoutinflater.inflate(R.layout.notes_list_view, parent, false);
            return new NoteListHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteListHolder viewHolder, Cursor cursor) {
            viewHolder.mTextInput.setText(cursor.getString(2));
        }
    }


}


