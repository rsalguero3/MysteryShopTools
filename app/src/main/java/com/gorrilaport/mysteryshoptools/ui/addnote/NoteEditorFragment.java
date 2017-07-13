package com.gorrilaport.mysteryshoptools.ui.addnote;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.listeners.OnCategorySelectedListener;
import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.category.SelectCategoryDialogFragment;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListActivity;
import com.gorrilaport.mysteryshoptools.ui.sketch.SketchActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.gorrilaport.mysteryshoptools.util.FileUtils;
import com.marshalchen.ultimaterecyclerview.ui.swipe.SwipeableRecyclerViewTouchListener;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteEditorFragment extends Fragment implements AddNoteContract.View {

    private View mRootView;
    private SelectCategoryDialogFragment selectCategoryDialog;
    private Note mCurrentNote = null;
    private boolean showLinedEditor = false;
    private final static String LOG_TAG = "NoteEditorFragment";


    @BindView(R.id.edit_text_category)
    EditText mCategory;
    @BindView(R.id.edit_text_title) EditText mTitle;
    @BindView(R.id.edit_text_note) EditText mContent;
    @BindView(R.id.image_attachment)
    ImageView mImageAttachment;
    @BindView(R.id.sketch_attachment) ImageView mSketchAttachment;

    private final int EXTERNAL_PERMISSION_REQUEST = 1;
    private final int RECORD_AUDIO_PERMISSION_REQUEST = 2;
    private final int IMAGE_CAPTURE_REQUEST = 3;
    private final int SKETCH_CAPTURE_REQUEST = 4;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private String mLocalAudioFilePath = null;
    private String mLocalImagePath = null;
    private String mLocalSketchPath = null;
    private Calendar mReminderTime;


    private Uri mImageURI = null;

    private AddNotePresenter mPresenter;



    public NoteEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        showLinedEditor = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("default_editor", false);
        Log.d(LOG_TAG, "Line Editor Enabled ?: " + showLinedEditor);
    }

    public static NoteEditorFragment newInstance(long noteId){
        NoteEditorFragment fragment = new NoteEditorFragment();

        if (noteId > 0){
            Bundle args = new Bundle();
            args.putLong(Constants.NOTE_ID, noteId);
            fragment.setArguments(args);

        }

        return fragment;
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

        return mRootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null && getArguments().containsKey(Constants.NOTE_ID)) {
            long noteId = getArguments().getLong(Constants.NOTE_ID, 0);
            mPresenter = new AddNotePresenter(this, noteId);
        }else {
            mPresenter = new AddNotePresenter(this, 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkStatus();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PackageManager packageManager = getActivity().getPackageManager();
        switch (item.getItemId()){
            case R.id.action_save:
                validateAndSaveContent();
                break;
            case R.id.action_delete:
                mPresenter.onDeleteNoteButtonClicked();
                break;
            case R.id.action_camera:
                //Handle taking picture
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    if (isStoragePermissionGrantedForImage()){
                        takePhoto();
                    }
                }
                break;
            case R.id.action_record:
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
                    if (isStoragePermissionGranted()){
                        if (isRecordPermissionGranted()){
                            promptToStartRecording();
                        }
                    }

                }else {
                    makeToast(getString(R.string.error_no_mic));
                }
                break;
            case R.id.action_play:
                if (mLocalAudioFilePath == null) {
                    makeToast("No Recording found");
                } else {
                    startPlaying();
                }
                break;
            case R.id.action_sketch:
                if (isStoragePermissionGrantedForSketch()){
                    Intent sketchIntent = new Intent(getActivity(), SketchActivity.class);
                    startActivityForResult(sketchIntent, SKETCH_CAPTURE_REQUEST);
                }
                break;
            case R.id.action_reminder:
                if (mCurrentNote != null && mCurrentNote.getId()> 0){
                    showReminderDate();
                }else {
                    makeToast("Save note before adding a reminder");
                }
                break;
            case android.R.id.home:
                //The user clicked on back button, save the content only if
                //It is not empty.
               //set category to General if no category is chosen
                if (!TextUtils.isEmpty(mContent.getText().toString())
                        && !TextUtils.isEmpty(mTitle.getText().toString())) {

                    if (Build.VERSION.SDK_INT >= 21){
                        getActivity().onBackPressed();
                    }
                    else {
                        validateAndSaveContent();
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= 21){
                        getActivity().onBackPressed();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.edit_text_category)
    public void showSelectCategory(){
        mPresenter.onSelectCategory();
    }

    @Override
    public void populateNote(Note note) {
        mTitle.setText(note.getTitle());
        mTitle.setHint(R.string.placeholder_note_title);
        mContent.setText(note.getContent());
        mContent.setHint(R.string.placeholder_note_text);
        if (!TextUtils.isEmpty(note.getCategoryName())){
            mCategory.setText(note.getCategoryName());
        }else {
            mCategory.setText(Constants.DEFAULT_CATEGORY);
        }

        if (!TextUtils.isEmpty(note.getLocalAudioPath())){
            mLocalAudioFilePath = note.getLocalAudioPath();
        }
        if (!TextUtils.isEmpty(note.getLocalImagePath())){
            mLocalImagePath = note.getLocalImagePath();
            populateImage(mLocalImagePath);
        }
        if (!TextUtils.isEmpty(note.getLocalSketchImagePath())){
            mLocalSketchPath = note.getLocalSketchImagePath();
           populateSketch(mLocalSketchPath);
        }
    }

    @Override
    public void setCurrentNote(Note note) {
        this.mCurrentNote = note;
    }


    @Override
    public void displayCategory(String category) {
        mCategory.setText(category);
    }

    @Override
    public void displayDeleteConfirmation(Note note) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(note);
        } else {
            mPresenter.deleteNote();
            displayPreviousActivity();
        }

    }


    private void promptForDelete(Note note){
        String title = "Delete " + note.getTitle();
        String message =  "Are you sure you want to delete note " + note.getTitle() + "?";


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.show();
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getActivity(), NoteListActivity.class));
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void displayDiscardConfirmation() {
        promptForDiscard();

    }

    @Override
    public void displayPreviousActivity() {
        startActivity(new Intent(getActivity(), NoteListActivity.class));
    }

    @Override
    public void displayMessage(String message) {
        makeToast(message);

    }

    @Override
    public void displayChooseCategoryDialog(List<Category> categories) {
        selectCategoryDialog = SelectCategoryDialogFragment.newInstance();
        selectCategoryDialog.setCategories(categories);

        selectCategoryDialog.setCategorySelectedListener(new OnCategorySelectedListener() {
            @Override
            public void onCategorySelected(Category selectedCategory) {
                selectCategoryDialog.dismiss();
                displayCategory(selectedCategory.getTitle());
            }

            @Override
            public void onEditCategoryButtonClicked(Category selectedCategory) {

            }

            @Override
            public void onDeleteCategoryButtonClicked(Category selectedCategory) {

            }
        });
        selectCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");


    }

    @Override
    public void finish() {
        startActivity(new Intent(getActivity(), NoteListActivity.class));

    }

    public void showReminderDate() {
        DialogFragment reminderDatePicker = new ReminderDatePickerDialogFragment();
        reminderDatePicker.setTargetFragment(NoteEditorFragment.this, 0);
        reminderDatePicker.show(getFragmentManager(), "reminderDatePicker");

    }

    public void showReminderTime() {
        DialogFragment reminderTimePicker = new ReminderTimePickerDialogFragment();
        reminderTimePicker.setTargetFragment(NoteEditorFragment.this, 0);
        reminderTimePicker.show(getFragmentManager(), "reminderTimePicker");

    }

    private void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        Gson gson = new Gson();
        String serializedNote = gson.toJson(mCurrentNote);
        intent.putExtra(Constants.SERIALIZED_NOTE, serializedNote);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, mReminderTime.getTimeInMillis(), alarmIntent);


        mCurrentNote.setNoteType(Constants.NOTE_TYPE_REMINDER);
        mCurrentNote.setNextReminder(mReminderTime.getTimeInMillis());
        addNoteToDatabase("Reminder set");


    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        File recordFile = FileUtils.getattachmentFileName(Constants.MIME_TYPE_AUDIO_EXT);
        mLocalAudioFilePath = recordFile.getAbsolutePath();
        mRecorder.setOutputFile(mLocalAudioFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(96000);
        mRecorder.setAudioSamplingRate(44100);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            makeToast("Unable to record " + e.getLocalizedMessage());
        }


    }


    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        makeToast("Recording added");
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mLocalAudioFilePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }


    public void promptToStartRecording(){
        String title = getContext().getString(R.string.start_recording);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);


        alertDialog.setPositiveButton(getString(R.string.start), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startRecording();
                promptToStopRecording();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void promptToStopRecording(){
        String title = getContext().getString(R.string.stop_recording);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);


        alertDialog.setPositiveButton(getString(R.string.stop), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopRecording();
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }




    public void validateAndSaveContent() {
        String category = mCategory.getText().toString();
        if (TextUtils.isEmpty(category)){
            mCategory.setText(Constants.DEFAULT_CATEGORY);
        }

        String title = mTitle.getText().toString();
        if (TextUtils.isEmpty(title)){
            mTitle.setError(getString(R.string.title_is_required));
            return;
        }

        String content = mContent.getText().toString();
        if (TextUtils.isEmpty(content)){
            mContent.setError(getString(R.string.note_is_required));
            return;
        }


        addNoteToDatabase("");
    }

    private void addNoteToDatabase(String message) {

        mCurrentNote.setContent(mContent.getText().toString());
        mCurrentNote.setTitle(mTitle.getText().toString());
        mCurrentNote.setDateModified(System.currentTimeMillis());
        mCurrentNote.setLocalAudioPath(mLocalAudioFilePath);
        mCurrentNote.setLocalImagePath(mLocalImagePath);
        mCurrentNote.setLocalSketchImagePath(mLocalSketchPath);
        mCurrentNote.setCategoryName(mCategory.getText().toString());

        if (!TextUtils.isEmpty(mLocalAudioFilePath)){
            mCurrentNote.setNoteType(Constants.NOTE_TYPE_AUDIO);
        }else if (!TextUtils.isEmpty(mLocalImagePath)){
            mCurrentNote.setNoteType(Constants.NOTE_TYPE_IMAGE);
        } else if (mReminderTime != null){

        }
        else {
            mCurrentNote.setNoteType("text");
        }

        mPresenter.onAddClick(mCurrentNote);

    }



    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void promptForDiscard(){
        String title = "Discard Note";
        String message =  "Are you sure you want to discard note ";


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetFields();
                startActivity(new Intent(getActivity(), NoteListActivity.class));
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

    private void resetFields() {
        mCategory.setText("");
        mTitle.setText("");
        mContent.setText("");
    }

    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_PERMISSION_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted  API < 23");
            return true;
        }
    }

    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isRecordPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGrantedForImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_CAPTURE_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted  API < 23");
            return true;
        }
    }

    private boolean isStoragePermissionGrantedForSketch() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SKETCH_CAPTURE_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted  API < 23");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isRecordPermissionGranted()) {
                        promptToStartRecording();
                    }
                } else {
                    //permission was denied, disable backup
                    makeToast("External storage access denied");
                }
                break;
            case RECORD_AUDIO_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    promptToStartRecording();
                } else {
                    //permission was denied, disable backup
                    makeToast("Mic access denied");
                }
                break;
            case IMAGE_CAPTURE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted perform backup
                    takePhoto();
                } else {
                    //permission was denied, disable backup
                    makeToast("External storage access denied");
                }
                break;
            case SKETCH_CAPTURE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted perform backup
                    Intent sketchIntent = new Intent(getActivity(), SketchActivity.class);
                    startActivityForResult(sketchIntent, SKETCH_CAPTURE_REQUEST);
                } else {
                    //permission was denied, disable backup
                    makeToast("External storage access denied");
                }
                break;


        }


    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK ){
            switch (requestCode){
                case IMAGE_CAPTURE_REQUEST:
                    addPhotoToGallery(mLocalImagePath);
                    populateImage(mLocalImagePath);
                    break;
                case SKETCH_CAPTURE_REQUEST:
                    mLocalSketchPath = data.getData().toString();
                    addPhotoToGallery(mLocalSketchPath);
                    if (!TextUtils.isEmpty(mLocalSketchPath)) {
                        populateSketch(mLocalSketchPath);
                    } else {
                        makeToast("Sketch is empty");
                    }
                    break;
            }
        }

    }

    private void addPhotoToGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);

    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = FileUtils.createImageFile();
            mLocalImagePath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
            // Error occurred while creating the File
            makeToast("There was a problem saving the photo...");
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri fileUri = Uri.fromFile(photoFile);
            mImageURI = fileUri;
            mLocalImagePath = fileUri.getPath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI);
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST);
        };
    }


    private void populateImage(String profileImagePath) {
        mImageAttachment.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(profileImagePath)
                .placeholder(R.drawable.person_icon)
                .centerCrop()
                .into(mImageAttachment);

    }

    private void populateSketch(String sketchImagePath) {
        mSketchAttachment.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(sketchImagePath)
                .placeholder(R.drawable.person_icon)
                .centerCrop()
                .into(mSketchAttachment);
    }

    public static class ReminderDatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            NoteEditorFragment targetFragment = (NoteEditorFragment)getTargetFragment();
            if (year < 0){
                targetFragment = null;
            } else {
                targetFragment.mReminderTime = Calendar.getInstance();
                targetFragment.mReminderTime.set(year, monthOfYear, dayOfMonth);
                targetFragment.showReminderTime();

            }

        }

    }



    public static class ReminderTimePickerDialogFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            NoteEditorFragment targetFragment = (NoteEditorFragment)getTargetFragment();
            final Calendar c = targetFragment.mReminderTime;
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            NoteEditorFragment targetFragment = (NoteEditorFragment)getTargetFragment();
            targetFragment.mReminderTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            targetFragment.mReminderTime.set(Calendar.MINUTE, minute);
            targetFragment.setAlarm();
        }

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
            Animation a = new Animation() {};
            a.setDuration(0);
            return a;

    }






}
