package com.gorrilaport.mysteryshoptools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class EditNoteFragment extends Fragment {
    private Toolbar mToolbar;
    private View mRootView;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private Note mCurrentNote = null;
    private Context mContext;


        public static EditNoteFragment newInstance(long id){
            EditNoteFragment fragment = new EditNoteFragment();

            if (id > 0){
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                fragment.setArguments(bundle);
            }
            return fragment;
        }

        private void getCurrentNote(){
            Bundle args = getArguments();
            if (args != null && args.containsKey("id")){
                long id = args.getLong("id", 0);
                if (id > 0){
                    mCurrentNote = NoteManager.newInstance(getActivity()).getNote(id);
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            getCurrentNote();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            mRootView = inflater.inflate(R.layout.fragment_edit_note, container, false);
            mTitleEditText = (EditText)mRootView.findViewById(R.id.edit_text_title);
            mContentEditText = (EditText)mRootView.findViewById(R.id.edit_text_note);
            return mRootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mCurrentNote != null){
                populateFields();
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            menu.clear();
            inflater.inflate(R.menu.menu_add_notes, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_delete:
                    //delete note
                    if (mCurrentNote != null){
                        promptForDelete();
                    }else {
                        makeToast("Cannot delete note that has not been saved");
                    }
                    break;
                case R.id.action_save:
                    //save note
                    if (saveNote()){
                        makeToast(mCurrentNote !=  null ? "Note updated" : "Note saved");
                    }
                    break;
            }
            return super.onOptionsItemSelected(item);
        }



        private void populateFields() {
            mTitleEditText.setText(mCurrentNote.getTitle());
            mContentEditText.setText(mCurrentNote.getTextInput());
        }


        private boolean saveNote(){

            String title = mTitleEditText.getText().toString();
            if (TextUtils.isEmpty(title)){
                mTitleEditText.setError("Title is required");
                return false;
            }

            String content = mContentEditText.getText().toString();
            if (TextUtils.isEmpty(content)){
                mContentEditText.setError("Content is required");
                return false;
            }


            if (mCurrentNote != null){
                mCurrentNote.setTextInput(content);
                mCurrentNote.setTitle(title);
                NoteManager.newInstance(getActivity()).update(mCurrentNote);

            }else {
                Note note = new Note();
                note.setTitle(title);
                note.setTextInput(content);
                NoteManager.newInstance(getActivity()).create(note);
            }
            return true;

        }

        private void makeToast(String message){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        public void promptForDelete(){
            final String titleOfNoteTobeDeleted = mCurrentNote.getTitle();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Delete " + titleOfNoteTobeDeleted + " ?");
            alertDialog.setMessage("Are you sure you want to delete the note " + titleOfNoteTobeDeleted + "?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NoteManager.newInstance(getActivity()).delete(mCurrentNote);
                    makeToast(titleOfNoteTobeDeleted + "deleted");
                    startActivity(new Intent(getActivity(), MainActivity.class));
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

    @TargetApi(21)
    private void setupWindowAnimations(){
        Fade fade = new Fade();
        fade.setDuration(1000);
        getActivity().getWindow().setEnterTransition(fade);
    }
}
