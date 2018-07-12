package com.gorrilaport.mysteryshoptools.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.gorrilaport.mysteryshoptools.core.dagger.AppComponent;
import com.gorrilaport.mysteryshoptools.core.dagger.AppModule;
import com.gorrilaport.mysteryshoptools.core.dagger.DaggerAppComponent;
import com.gorrilaport.mysteryshoptools.core.services.AddSampleDateIntentService;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.gorrilaport.mysteryshoptools.util.TypefaceUtil;
import com.squareup.leakcanary.LeakCanary;
import com.thefinestartist.Base;

import io.fabric.sdk.android.Fabric;

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
        Base.initialize(this);
        Fabric.with(this, new Crashlytics());
        TypefaceProvider.registerDefaultIconSets();
        LeakCanary.install(this);
        getAppComponent();
        addDefaultData();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Raleway-Medium.ttf");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }
        return appComponent;
    }

    public void rebuildAppComponent() {
        appComponent = null;
    }


    //Checks if this is the first time this app is running and then
    //starts an Intent Services that adds some default data
    private void addDefaultData() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //If first run is true show login in page, after user reenters app show notelist activity
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(Constants.FIRST_RUN, true)) {
            startService(new Intent(this, AddSampleDateIntentService.class));
        }
    }

    public void resetApplication(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.FIRST_RUN, true).commit();
    }
}
