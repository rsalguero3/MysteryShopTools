package com.gorrilaport.mysteryshoptools.ui.category;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.listeners.OnCategoryAddedListener;
import com.gorrilaport.mysteryshoptools.model.Category;
import com.gorrilaport.mysteryshoptools.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditCategoryDialogFragment extends DialogFragment {
    private EditText mCategoryEditText;
    private Category mCategory;
    private boolean mInEditMode = false;
    private ImageView mColorView;
    private OnCategoryAddedListener mListener;

    public AddEditCategoryDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setListener(OnCategoryAddedListener mListener) {
        this.mListener = mListener;
    }

    public static AddEditCategoryDialogFragment newInstance(String content){
        AddEditCategoryDialogFragment dialogFragment = new AddEditCategoryDialogFragment();
        Bundle args = new Bundle();

        if (!content.isEmpty()){
            args.putString(Constants.SERIALIZED_CATEGORY, content);
            dialogFragment.setArguments(args);
        }

        return dialogFragment;
    }

    /**
     * The method gets the Category that was passed in, in the form of serialized String
     * if nothing was passed in then it will create a new Category
     *
     */
    public void getCurrentCategory(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_CATEGORY)){
            String serializedCategory = args.getString(Constants.SERIALIZED_CATEGORY, "");
            if (!serializedCategory.isEmpty()){
                Gson gson = new Gson();
                mCategory = gson.fromJson(serializedCategory, new TypeToken<Category>(){}.getType());
                if (mCategory != null & mCategory.getId() > 0){
                    mInEditMode = true;
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder addCategoryDialog = new AlertDialog.Builder(getActivity());


        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_edit_category_dialog, null);
            mColorView = convertView.findViewById(R.id.color_view);
            convertView.findViewById(R.id.category_color_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupColorChooser(mColorView);
                }
            });
            addCategoryDialog.setView(convertView);

            getCurrentCategory();
            if(mInEditMode == false){
                mColorView.setBackgroundColor(-1);
            }

            View titleView = inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mInEditMode == true ? getString(R.string.edit_category) : getString(R.string.add_category));
            addCategoryDialog.setCustomTitle(titleView);

            mCategoryEditText = convertView.findViewById(R.id.edit_text_add_category);


            addCategoryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addCategoryDialog.setPositiveButton(mInEditMode == true ? "Update" : "Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });



            if (mInEditMode){
                populateFields(mCategory);
                addCategoryDialog.setTitle(mCategory.getTitle());
            }


        }

        return addCategoryDialog.create();
    }

    private void populateFields(Category category) {
        mCategoryEditText.setText(category.getTitle());
        mColorView.setBackgroundColor(category.getColor());
    }

    private boolean requiredFieldCompleted(){
        if (mCategoryEditText.getText().toString().isEmpty())
        {
            mCategoryEditText.setError(getString(R.string.name_is_required));
            mCategoryEditText.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();


        if (d != null){
            Button positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean readyToCloseDialog = false;
                    if (requiredFieldCompleted()) {
                        saveCategory();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog)
                        dismiss();
                }
            });
        }
    }



    private void saveCategory() {
        if (mInEditMode){
            if (mCategory != null){
                mCategory.setTitle(mCategoryEditText.getText().toString().trim());
                mCategory.setColor((((ColorDrawable) mColorView.getBackground()).getColor()));
                mListener.onCategoryAdded(mCategory);
            }
        }else {
            Category addedCategory = new Category();
            addedCategory.setTitle(mCategoryEditText.getText().toString().trim());
            addedCategory.setColor(((ColorDrawable) mColorView.getBackground()).getColor());
            mListener.onCategoryAdded(addedCategory);
        }

    }


    private void setupColorChooser(final ImageView view){
        ColorPickerDialogBuilder
                .with(getActivity())
                .setTitle("Choose color")
                .initialColor(R.color.white)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(8)
                .noSliders()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        System.out.println("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        view.setBackgroundColor(selectedColor);
                        System.out.println("color changed");
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

}
