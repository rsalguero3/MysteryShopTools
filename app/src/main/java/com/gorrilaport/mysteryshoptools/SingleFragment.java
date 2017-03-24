package com.gorrilaport.mysteryshoptools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ricardo on 3/6/2016.
 */
public class SingleFragment extends Fragment {
    private int mLayoutId;
    private Bundle mBundle;
    private View mrootView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Find the fragment key to find which layout to use
        mBundle = getArguments();
        mLayoutId = mBundle.getInt(MainActivity.getFragmentKey());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
        //save the reference of layout
        mrootView = inflater.inflate(mLayoutId, container, false);
        return mrootView;
    }

    public View getRootView(){
        return mrootView;
    }
}