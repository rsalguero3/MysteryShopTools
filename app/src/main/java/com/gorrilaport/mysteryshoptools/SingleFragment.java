package com.gorrilaport.mysteryshoptools;

import android.content.Context;
import android.os.Bundle;
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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        mLayoutId = mBundle.getInt(MainActivity.getFragmentKey());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ){
        View v = inflater.inflate(mLayoutId, container, false);
        return v;
    }
}
