package com.gorrilaport.mysteryshoptools.ui.notelist;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.core.listeners.NoteItemListener;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.addnote.AddNoteActivity;
import com.gorrilaport.mysteryshoptools.ui.notedetail.NoteDetailActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements NoteListContract.View {


    private NoteListContract.Actions mPresenter;
    private NoteListAdapter mListAdapter;


    @Inject
    EventBus mBus;
    @Inject
    SharedPreferences mSharedPreference;

    private RecyclerView mRecyclerView;
    private TextView mEmptyText;

    private View mRootView;
    private FloatingActionButton mFab;



    public NoteListFragment() {
        // Required empty public constructor
    }


    public static NoteListFragment newInstance(boolean dualScreen){
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_DUAL_SCREEN, dualScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.note_recycler_view);
        mEmptyText = (TextView) mRootView.findViewById(R.id.empty_text);

        ButterKnife.bind(this, mRootView);
        MysteryShopTools.getInstance().getAppComponent().inject(this);
//        mBus.register(this);

        mPresenter = new NotesListPresenter(this);
        mListAdapter = new NoteListAdapter(new ArrayList<Note>(), getContext());
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mListAdapter.setNoteItemListener(new NoteItemListener() {
            @Override
            public void onNoteClick(Note clickedNote) {
                mPresenter.openNoteDetails(clickedNote.getId());
            }

            @Override
            public void onDeleteButtonClicked(Note clickedNote) {
                mPresenter.onDeleteNoteButtonClicked(clickedNote);
            }
        });

        mFab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onAddNewNoteButtonClicked();
            }
        });
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args  != null && args.containsKey(Constants.IS_DUAL_SCREEN)){
            mPresenter.setLayoutMode(args.getBoolean(Constants.IS_DUAL_SCREEN));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadNotes();
    }

    @Override
    public void showNotes(List<Note> notes) {
        mListAdapter.replaceData(notes);
    }

    @Override
    public void showAddNote() {
        startActivity(new Intent(getActivity(), AddNoteActivity.class));
    }


    @Override
    public void showSingleDetailUi(long noteId) {
        startActivity(NoteDetailActivity.getStartIntent(getContext(), noteId));
    }

    @Override
    public void showDualDetailUi(Note note) {
        NoteListActivity activity = (NoteListActivity)getActivity();
        activity.showTwoPane(note);

    }

    @Override
    public void showEmptyText(boolean showText) {
        if (showText){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void showDeleteConfirmation(Note note) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(note);
        } else {
            mPresenter.deleteNote(note);
        }
    }

    /**
     * Displays any message passed from the Presenter
     * @param message
     */
    @Override
    public void showMessage(String message) {
        makeToast(message);
    }



    public void promptForDelete(final Note note){
        String title = "Delete " + note.getTitle();
        String content = note.getContent();
        String message =  "Delete " + content.substring(0, Math.min(content.length(), 50)) + "  ... ?";


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteNote(note);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
