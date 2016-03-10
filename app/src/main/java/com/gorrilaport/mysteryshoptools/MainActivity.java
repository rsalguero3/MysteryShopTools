package com.gorrilaport.mysteryshoptools;

import android.content.Intent;
import android.content.res.Configuration;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private Bundle mFragmentBundle;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private static String mFragmentKey;
    ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fm = getSupportFragmentManager();


    private static final String MAIN_MENU_FRAGMENT = "main_menu_fragment";
    private static final String NOTE_TAKING_FRAGMENT = "note_taking_fragment";
    private static final String CAMERA_FRAGMENT = "camera_fragment";
    private static final String TIMER_FRAGMENT = "timer_fragment";

    public static String getFragmentKey(){
        return mFragmentKey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        mDrawer = (NavigationView) findViewById(R.id.navList);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawer.setItemIconTintList(null);

        // Initiate Fragment Manager and create fragments
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new SingleFragment();
            mFragmentKey = MAIN_MENU_FRAGMENT;
            mFragmentBundle = new Bundle();
            mFragmentBundle.putInt(MAIN_MENU_FRAGMENT, R.layout.fragment_main_menu);
            fragment.setArguments(mFragmentBundle);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note_taking_menu) {
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            fragment = new NotepadFragment();
            createFragment(NOTE_TAKING_FRAGMENT, R.layout.fragment_notepad, R.id.fragment_container, fragment);
            return true;
        }
        else if (item.getItemId() == R.id.timer_menu) {
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            fragment = new TimerFragment();
            createFragment(TIMER_FRAGMENT, R.layout.fragment_timer, R.id.fragment_container, fragment);
            return true;
        }
        else if (item.getItemId() == R.id.camera_menu) {
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            fragment = new CameraFragment();
            createFragment(CAMERA_FRAGMENT, R.layout.fragment_camera, R.id.fragment_container, fragment);
            return true;

        }
        else return false;
    }
    // Creates fragment do ensure fragment key can be received by SingleFragment class
    private void createFragment(String fragmentKey, int layout, int container, Fragment fragment ){
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mFragmentBundle = new Bundle();
        mFragmentKey = fragmentKey;
        mFragmentBundle.putInt(fragmentKey, layout);
        fragment.setArguments(mFragmentBundle);
        setFragment(container, fragment);
    }

    private void setFragment(int container, Fragment fragment){
        fm.popBackStackImmediate();
        fm.beginTransaction().replace(container, fragment).commit();
    }
}



