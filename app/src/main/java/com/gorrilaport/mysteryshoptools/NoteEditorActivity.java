package com.gorrilaport.mysteryshoptools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentTransaction;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.Toolbar;
    import android.view.Menu;
    import android.view.MenuItem;


    public class NoteEditorActivity extends AppCompatActivity {

        private Toolbar mToolbar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_note_editor);


            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //remove this line in the MainActivity.java

            if (savedInstanceState == null) {
                Bundle args = getIntent().getExtras();
                if (args != null && args.containsKey("id")) {
                    long id = args.getLong("id", 0);
                    if (id > 0) {
                        openFragment(EditNoteFragment.newInstance(id), "Editor");
                    }
                }
                openFragment(EditNoteFragment.newInstance(0), "Editor");
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_note_editor, menu);
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

        private void openFragment(final Fragment fragment, String title) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
            getSupportActionBar().setTitle(title);
        }
    }

