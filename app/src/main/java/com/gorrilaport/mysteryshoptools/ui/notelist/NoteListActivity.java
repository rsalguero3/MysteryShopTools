package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.artitk.licensefragment.model.License;
import com.artitk.licensefragment.model.LicenseType;
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gorrilaport.mysteryshoptools.auth.AuthUiActivity;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.ui.camera.CameraFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.ui.category.CategoryActivity;
import com.gorrilaport.mysteryshoptools.ui.notedetail.NoteDetailFragment;
import com.gorrilaport.mysteryshoptools.ui.settings.SettingsActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListActivity extends AppCompatActivity implements ActionMode.Callback {
    public Toolbar toolbar;
    private AccountHeader headerResult;
    private Drawer drawer;
    private String mUsername;
    private String mPhotoUrl;
    private String mEmailAddress;
    private FirebaseUser mFirebaseUser;

    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(android.R.id.content) View mRootView;

    @Inject
    NoteListContract.FirebaseRepository mFirebaseRepository;
    @Inject NoteListContract.Repository mRepository;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public NoteListActivity() {
        MysteryShopTools.getInstance().getAppComponent().inject(this);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNavigationDrawer(savedInstanceState);



        if(mFirebaseRepository.getFirebaseUser() == null){
            //Not signed in, launch the sign in activity
            startActivity(new Intent(this, AuthUiActivity.class));
            finish();
            return;
        }
        else {
            mFirebaseUser = mFirebaseRepository.getFirebaseUser();
            mUsername = mFirebaseUser.getDisplayName();
            //mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            mEmailAddress = mFirebaseUser.getEmail();
        }

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
        mUsername = TextUtils.isEmpty(mUsername) ? "Anonymous" : mUsername;
        mEmailAddress = TextUtils.isEmpty(mEmailAddress) ? "Email" : mEmailAddress;
        mPhotoUrl = TextUtils.isEmpty(mPhotoUrl) ? "R.drawable.anon_user_48dp" : mPhotoUrl;

        IProfile profile = new ProfileDrawerItem()
                .withName(mUsername)
                .withEmail("someemail@gymmail.com")
                .withIcon(mPhotoUrl)
                .withIdentifier(102);

        //Create Navigation drawer Account Header
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_3)
                .addProfiles(profile)
                .build();

        drawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_drawer_notes).withIcon(FontAwesome.Icon.faw_sticky_note).withIdentifier(Constants.NOTE),
                        new PrimaryDrawerItem().withName(R.string.menu_drawer_categories).withIcon(FontAwesome.Icon.faw_folder).withIdentifier(Constants.CATEGORY),
                        //new PrimaryDrawerItem().withName("Camera").withIcon(FontAwesome.Icon.faw_folder).withIdentifier(Constants.CAMERA),
                        new PrimaryDrawerItem().withName(R.string.menu_drawer_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(Constants.SETTINGS),
                        //new PrimaryDrawerItem().withName("Timer").withIcon(FontAwesome.Icon.faw_book).withIdentifier(Constants.TIMER)
                        new PrimaryDrawerItem().withName(R.string.menu_drawer_companies).withIcon(FontAwesome.Icon.faw_industry).withIdentifier(Constants.COMPANIES),
                        new PrimaryDrawerItem().withName(R.string.menu_drawer_about).withIcon(FontAwesome.Icon.faw_database).withIdentifier(Constants.ABOUT),
                        new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_lock).withIdentifier(Constants.LOGOUT)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
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
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
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
        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName(R.string.delete_account).withIcon(GoogleMaterial.Icon.gmd_delete).withIdentifier(Constants.DELETE_ACCOUNT));
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
            case Constants.CAMERA:
                openFragment(CameraFragment.newInstance(), "Camera");
                break;
            case Constants.COMPANIES:
                mFab.hide();
                openFragment(CompaniesFragment.newInstance(), "Companies");
                break;
            case Constants.ABOUT:
                ArrayList<Integer> licenseIds = new ArrayList<>();
                RecyclerViewLicenseFragment f = RecyclerViewLicenseFragment.newInstance(licenseIds);
                f.addCustomLicense(createLicenses());
                mFab.hide();
                openFragment(f, "About");
                break;
            case Constants.LOGOUT:
                logout();
                break;
            case Constants.DELETE_ACCOUNT:
                deleteAccountClicked();
        }

    }

    private void deleteAccountClicked() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .create();

        dialog.show();
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mRepository.deleteDatabase();
                            startActivity(new Intent(NoteListActivity.this, AuthUiActivity.class));
                            if (Build.VERSION.SDK_INT >= 21){
                                finishAfterTransition();
                            }
                            else {
                                finish();
                            }
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });

    }

    private void deleteAccount() {
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(NoteListActivity.this, AuthUiActivity.class));
                            finish();
                        } else {
                            showSnackbar(R.string.delete_account_failed);
                        }
                    }
                });
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

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}
