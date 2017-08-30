package com.gorrilaport.mysteryshoptools.ui.notedetail;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.listeners.OnEditNoteButtonClickedListener;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.gorrilaport.mysteryshoptools.util.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDetailFragment extends Fragment implements NoteDetailContract.View {

    private View mRootView;
    @BindView(R.id.edit_text_title) EditText mTitle;
    @BindView(R.id.edit_text_note) EditText mContent;
    @BindView(R.id.edit_text_category) EditText mCategory;
    @BindView(R.id.image_attachment) ImageView mImageAttachment;
    @BindView(R.id.sketch_attachment) ImageView mSketchAttachment;
    @BindView(R.id.time_stamp) TextView mTimeStamp;

    private NoteDetailPresenter mPresenter;
    private OnEditNoteButtonClickedListener mListener;

    private boolean showLinedEditor = false;

    public NoteDetailFragment() {
        // Required empty public constructor
    }

    public static NoteDetailFragment newInstance(long noteId){
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        showLinedEditor = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("default_editor", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (showLinedEditor){
            mRootView = inflater.inflate(R.layout.fragment_lined_editor, container, false);
        }else {
            mRootView = inflater.inflate(R.layout.fragment_plain_editor, container, false);
        }

        ButterKnife.bind(this, mRootView);
        displayReadOnlyViews();
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null && getArguments().containsKey(Constants.NOTE_ID)) {
            long noteId = getArguments().getLong(Constants.NOTE_ID, 0);
            mPresenter = new NoteDetailPresenter(this, noteId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mPresenter.showNoteDetails();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_edit:
                mListener.onEditNote(mPresenter.getCurrentNote());
                break;
            case R.id.action_delete:
                mPresenter.onDeleteNoteButtonClicked();
                break;
            case R.id.action_share:
                mPresenter.onShareButtonClicked();
                break;
            case R.id.action_play:
                mPresenter.onPlayAudioButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void displayNote(Note note) {
        mCategory.setText(note.getCategoryName());
        mContent.setText(note.getContent());
        mTitle.setText(note.getTitle());
        mTimeStamp.setText("Date Created: " + TimeUtils.getReadableModifiedDate(note.getDateCreated())
        + "\n" + "Date Modified: " + TimeUtils.getReadableModifiedDate(note.getDateModified()));

        if (!TextUtils.isEmpty(note.getLocalImagePath())){
            populateImage(note.getLocalImagePath());
        }

        if (!TextUtils.isEmpty(note.getLocalSketchImagePath())){
             populateSketch(note.getLocalSketchImagePath());
        }
    }

    @Override
    public void showEditNoteScreen(long noteId) {

    }

    @Override
    public void showDeleteConfirmation(Note note) {

    }

    @Override
    public void displayShareIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle.getText().toString());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, mContent.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }
    @Override
    public void displayPreviousActivity() {
        getActivity().onBackPressed();
    }

    @Override
    public void showMessage(String message) {
        makeToast(message);
    }

    @Override
    public void displayReadOnlyViews() {
        mCategory.setFocusable(false);
        mTitle.setFocusable(false);
        mContent.setFocusable(false);
        mCategory.setOnClickListener(this.onClickListener());
        mTitle.setOnClickListener(this.onClickListener());
        mContent.setOnClickListener(this.onClickListener());
        mImageAttachment.setOnClickListener(this.onClickListenerImage());
        mTimeStamp.setFocusable(false);
    }

    @Override
    public void finish() {
        startActivity(new Intent(getActivity(), NoteListActivity.class));
    }

    public void promptForDelete(Note note){
        final String titleOfNoteTobeDeleted = note.getTitle();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        //alertDialog.setTitle("Delete " + titleOfNoteTobeDeleted + " ?");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText("Delete " + titleOfNoteTobeDeleted + " ?");
        alertDialog.setCustomTitle(titleView);


        alertDialog.setMessage("Are you sure you want to delete the note " + titleOfNoteTobeDeleted + "?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteNote();
                displayPreviousActivity();
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

    public void setListener(OnEditNoteButtonClickedListener mListener) {
        this.mListener = mListener;
    }

    private void populateImage(String profileImagePath) {
        mImageAttachment.setVisibility(View.VISIBLE);
        Glide.with(getActivity())
                .load(profileImagePath)
                .into(mImageAttachment);
    }

    private void populateSketch(String sketchImagePath) {
        mSketchAttachment.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(sketchImagePath)
                .into(mSketchAttachment);
    }

    private View.OnClickListener onClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onEditNote(mPresenter.getCurrentNote());
        }
    };
        return listener;
}


    private View.OnClickListener onClickListenerImage() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.image_attachment) {
                    if (mImageAttachment.isShown()) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        //intent.setType("image/*");
                        intent.setDataAndType(Uri.parse(mPresenter.getCurrentNote().getLocalImagePath()), "image/*");
                        startActivity(intent);
                    }
                }
            }
        };
        return listener;
    }
}
