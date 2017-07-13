package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;

import com.gorrilaport.mysteryshoptools.ui.camera.CameraFragment;
import com.gorrilaport.mysteryshoptools.ui.timer.TimerFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryActivity;
import com.gorrilaport.mysteryshoptools.ui.notedetail.NoteDetailFragment;
import com.gorrilaport.mysteryshoptools.ui.settings.SettingsActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;

public class NoteListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AccountHeader headerResult;
    private Drawer drawer;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        if (Build.VERSION.SDK_INT >= 21){
            setupWindowAnimations();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNavigationDrawer(savedInstanceState);

        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w800dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            findViewById(R.id.note_detail_container).setVisibility(View.GONE);
        }

        openFragment(NoteListFragment.newInstance(mTwoPane), "Notes");



    }

    private void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }

    private void openDetailFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.note_detail_container, fragment)
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }


    private void setupNavigationDrawer(Bundle savedInstanceState) {
        //Create Navigation drawer Account Header
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_3)
                .build();

        drawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Notes").withIcon(FontAwesome.Icon.faw_sticky_note).withIdentifier(Constants.NOTE),
                        new PrimaryDrawerItem().withName("Categories").withIcon(FontAwesome.Icon.faw_folder).withIdentifier(Constants.CATEGORY),
                        //new PrimaryDrawerItem().withName("Camera").withIcon(FontAwesome.Icon.faw_folder).withIdentifier(Constants.CAMERA),
                        new PrimaryDrawerItem().withName("Settings").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(Constants.SETTINGS)
                        //new PrimaryDrawerItem().withName("Timer").withIcon(FontAwesome.Icon.faw_book).withIdentifier(Constants.TIMER)
                )
                .withOnDrawerItemClickListener(new com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable){
                            String name = ((Nameable)drawerItem).getName().getText(NoteListActivity.this);
                            toolbar.setTitle(name);
                        }

                        if (drawerItem != null){
                            //handle on navigation drawer item
                            onTouchDrawer((int) drawerItem.getIdentifier());
                        }
                        return false;
                    }


                })
                .withOnDrawerListener(new com.mikepenz.materialdrawer.Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        KeyboardUtil.hideKeyboard(NoteListActivity.this);

                    }

                    @Override
                    public void onDrawerClosed(View view) {

                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();



    }

    /**
     * Handles the selection of an item in the Navigation drawer
     * @param position of the selected item in the Navigation drawer
     */
    private void onTouchDrawer(final int position) {
        switch (position){
            case Constants.NOTE:
                  break;
            case Constants.SETTINGS:
                startActivity(new Intent(NoteListActivity.this, SettingsActivity.class));
                break;
            case Constants.CATEGORY:
                startActivity(new Intent(NoteListActivity.this, CategoryActivity.class));
                break;
            case Constants.TIMER:
                startActivity(new Intent(NoteListActivity.this, TimerFragment.class));
                break;
            case Constants.CAMERA:
                openFragment(CameraFragment.newInstance(), "Camera");
                break;
        }

    }

    public void showTwoPane(Note note){
        findViewById(R.id.note_detail_container).setVisibility(View.VISIBLE);
        openDetailFragment(NoteDetailFragment.newInstance(note.getId()), note.getTitle());


    }

    @Override
    public void onBackPressed(){
//        Toast toast = Toast.makeText(getApplicationContext(), "Saving",
//                Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
//        toast.show();
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        getSupportActionBar().setTitle("Notes");
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setExitTransition(slide);
    }
}
