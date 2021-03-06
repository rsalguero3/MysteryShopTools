package com.gorrilaport.mysteryshoptools.core;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gorrilaport.mysteryshoptools.core.dagger.AppComponent;
import com.gorrilaport.mysteryshoptools.core.dagger.AppModule;
import com.gorrilaport.mysteryshoptools.core.dagger.DaggerAppComponent;
import com.gorrilaport.mysteryshoptools.core.services.AddSampleDateIntentService;
import com.gorrilaport.mysteryshoptools.util.Constants;

public class MysteryShopTools extends Application {
    private static MysteryShopTools instance = new MysteryShopTools();
    private static AppComponent appComponent;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static MysteryShopTools getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
       getAppComponent();
       addDefaultData();

    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }

        return appComponent;
    }


    //Checks if this is the first time this app is running and then
    //starts an Intent Services that adds some default data
    private void addDefaultData() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(Constants.FIRST_RUN, true)) {
            startService(new Intent(this, AddSampleDateIntentService.class));
            editor.putBoolean(Constants.FIRST_RUN, false).commit();
        }

    }






}
