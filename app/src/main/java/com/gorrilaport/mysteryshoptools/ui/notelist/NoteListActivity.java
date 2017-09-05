package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.artitk.licensefragment.model.License;
import com.artitk.licensefragment.model.LicenseType;
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListActivity extends AppCompatActivity implements ActionMode.Callback {
    public Toolbar toolbar;
    private AccountHeader headerResult;
    private Drawer drawer;
    @BindView(R.id.fab)
    FloatingActionButton mfab;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        new SlidingRootNavBuilder(this)
//                .withToolbarMenuToggle(toolbar)
//                .withMenuOpened(true)
//                .withSavedState(savedInstanceState)
//                .withMenuLayout(R.layout.menu_left_drawer)
//                .inject();
        setupNavigationDrawer(savedInstanceState);
        setupDefaultFragment();
        ButterKnife.bind(this);
    }

    private void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
                        new PrimaryDrawerItem().withName("Settings").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(Constants.SETTINGS),
                        //new PrimaryDrawerItem().withName("Timer").withIcon(FontAwesome.Icon.faw_book).withIdentifier(Constants.TIMER)
                        new PrimaryDrawerItem().withName("Companies").withIcon(FontAwesome.Icon.faw_industry).withIdentifier(Constants.COMPANIES),
                        new PrimaryDrawerItem().withName("About").withIcon(FontAwesome.Icon.faw_database).withIdentifier(Constants.ABOUT)
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
                        drawer.deselect();
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
                setupDefaultFragment();
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
            case Constants.COMPANIES:
                mfab.hide();
                openFragment(CompaniesFragment.newInstance(), "Companies");
                break;
            case Constants.ABOUT:
                ArrayList<Integer> licenseIds = new ArrayList<>();
                RecyclerViewLicenseFragment f = RecyclerViewLicenseFragment.newInstance(licenseIds);
                f.addCustomLicense(createLicenses());
                mfab.hide();
                openFragment(f, "About");
        }
    }

    public void showTwoPane(Note note){
        findViewById(R.id.note_detail_container).setVisibility(View.VISIBLE);
        openDetailFragment(NoteDetailFragment.newInstance(note.getId()), note.getTitle());
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.note_list_activity_actionbar_name));
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    /**
     * Creates information to be displayed when showing the open sources licenses used in the app
     * @return An ArrayList of Licenses used
     */
    public ArrayList<License> createLicenses(){
        ArrayList<License> list = new ArrayList<>();
        list.add(new License(this, "Android-Iconics", LicenseType.APACHE_LICENSE_20, "2016", "Mike Penz"));
        list.add(new License(this, "Android StyleableToast", LicenseType.APACHE_LICENSE_20, "2017", "Muddii Walid (Muddz)"));
        list.add(new License(this, "Butter Knife", LicenseType.APACHE_LICENSE_20, "2013", "Jake Wharton"));
        list.add(new License(this, "TextDrawable", LicenseType.MIT_LICENSE, "2014", "Amulya Khare"));
        list.add(new License(this, "UltimateRecyclerView", LicenseType.APACHE_LICENSE_20, "2014", "Marshal Chen"));
        list.add(new License(this, "Glide", LicenseType.APACHE_LICENSE_20, "2014", "Sam Judd"));
        list.add(new License(this, "CircleImageView", LicenseType.APACHE_LICENSE_20, "2014", "Henning Dodenhof"));
        list.add(new License(this, "FButton", LicenseType.APACHE_LICENSE_20, "2014", "Le Van Hoang"));
        list.add(new License(this, "Android Swipe Layout", LicenseType.APACHE_LICENSE_20, "2014", "daimajia"));
        list.add(new License(this, "FinestWebView", LicenseType.APACHE_LICENSE_20, "2013", "TheFinestArtist"));
        list.add(new License(this, "SlidingRootNav", LicenseType.APACHE_LICENSE_20, "2017", "Yaroslav Shevchuk"));
        return list;
    }

    /**
     * Create the default view when the app first begins
     */
    public void setupDefaultFragment(){
        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w800dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            findViewById(R.id.note_detail_container).setVisibility(View.GONE);
        }
        openFragment(NoteListFragment.newInstance(mTwoPane), getString(R.string.note_list_activity_actionbar_name));
    }
}
