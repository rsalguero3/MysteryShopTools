package com.gorrilaport.mysteryshoptools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import static android.R.attr.fragment;


/**
 * Created by Ricardo on 3/6/2016.
 */
public class NotepadFragment extends Fragment {
    private FloatingActionButton mAddNoteAction;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteListAdapter mAdapter;
    private List<Note> mNotes;

    public static final int REQUEST_CODE_NOTEPAD_FRAG = 0;
    public Activity mActivity;

    public Cursor titleCursor;
    public Cursor textCursor;
    private View mRootView;
    private FloatingActionButton mFab;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
        //save the reference of layout
        mRootView = inflater.inflate(R.layout.fragment_notepad, container, false);
        mFab = (FloatingActionButton)mRootView.findViewById(R.id.fab_notepad);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NoteEditorActivity.class));
            }
        });
        setupList();

        return mRootView;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (Activity)context;
    }

    public void setupList(){
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.fragment_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final GestureDetector mGestureDectector =
                new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapUp(MotionEvent e){
                        return true;
                    }
                });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent){
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDectector.onTouchEvent(motionEvent)){
                    int positon = recyclerView.getChildLayoutPosition(child);
                    Note selectedNote = mNotes.get(positon);
                    EditNoteFragment editFragment = new EditNoteFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("id",selectedNote.getId());
                    editFragment.setArguments(bundle);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e){

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        mNotes = NoteManager.newInstance(getActivity()).getAllNotes();
        mAdapter = new NoteListAdapter(mNotes, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }
}


