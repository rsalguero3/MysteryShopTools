package com.gorrilaport.mysteryshoptools.ui.category;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.listeners.OnCategoryAddedListener;
import com.gorrilaport.mysteryshoptools.core.listeners.OnCategorySelectedListener;
import com.gorrilaport.mysteryshoptools.model.Category;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryListFragment extends Fragment implements
        CategoryListContract.View, OnCategorySelectedListener {


    private static final String LOG_CAT = CategoryListFragment.class.getSimpleName();
    private final static boolean DEBUG = true;

    private CategoryRecyclerViewAdapter mAdapter;
    private View mRootView;
    private CategoryListPresenter mPresenter;
    private AddEditCategoryDialogFragment addCategoryDialog;

    @BindView(R.id.category_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;


    public static CategoryListFragment newInstance(){
        return new CategoryListFragment();
    }

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        ButterKnife.bind(this, mRootView);
        mPresenter = new CategoryListPresenter(this);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewCategoryDialog();
            }
        });
        return  mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadCategories();
    }

    @Override
    public void showAddNewCategoryDialog() {
        addCategoryDialog = AddEditCategoryDialogFragment.newInstance("");
        addCategoryDialog.setListener(new OnCategoryAddedListener() {
            @Override
            public void onCategoryAdded(Category category) {
                mPresenter.addNewCategory(category);
            }
        });
        addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
    }

    @Override
    public void showCategory(String category) {

    }

    @Override
    public void showEmptyText() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyText() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.GONE);
    }

    @Override
    public void displayMessage(String message) {
        makeToast(message);
    }

    @Override
    public void showCategories(List<Category> categories,
                               Map<Long, Integer> noteCount
    ) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CategoryRecyclerViewAdapter(getContext(),categories, noteCount, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showConfirmDeleteCategoryPrompt(Category category) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(category);
        } else {
            mPresenter.deleteCategory(category);
        }
    }

    @Override
    public void showEditCategoryForm(Category category) {
        Gson gson = new Gson();
        String serializedCategory = gson.toJson(category);

        addCategoryDialog = AddEditCategoryDialogFragment.newInstance(serializedCategory);
        addCategoryDialog.setListener(new OnCategoryAddedListener() {
            @Override
            public void onCategoryAdded(Category category) {
                mPresenter.addNewCategory(category);
            }
        });
        addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
    }

    @Override
    public void onCategorySelected(Category selectedCategory) {
    }

    @Override
    public void onEditCategoryButtonClicked(Category selectedCategory) {
        mPresenter.onEditCategoryButtonClicked(selectedCategory);
    }

    @Override
    public void onDeleteCategoryButtonClicked(Category selectedCategory) {
        mPresenter.onDeleteCategoryButtonClicked(selectedCategory);
    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void promptForDelete(final Category category){
        String title = getString(R.string.delete_Category) + " ?";
        String message =  getString(R.string.action_delete) + " " + category.getTitle();

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.action_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteCategory(category);
            }
        });
        alertDialog.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
