package com.gorrilaport.mysteryshoptools;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {
    Button timer, notepad, voice_recorder, camera;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();



        //API < 21 use Buttons
        if (Build.VERSION.SDK_INT < 21) {
            createButtons();

            timer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Timer.class);
                    MainActivity.this.startActivity(intent);
                }
            });
        }
        //API >=21 set the List and Adapter in the activity

    }


    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

//Methods for API < 21
    private void createButtons() {
        timer = (Button) findViewById(R.id.timer);
        notepad = (Button) findViewById(R.id.notepad);
        voice_recorder = (Button) findViewById(R.id.voice_recorder);
        camera = (Button) findViewById(R.id.camera);
    }
}



