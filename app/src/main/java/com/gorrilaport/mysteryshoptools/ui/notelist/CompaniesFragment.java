package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gorrilaport.mysteryshoptools.R;
import com.thefinestartist.finestwebview.FinestWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompaniesFragment extends Fragment {

    private View mViewRoot;
    @BindView(R.id.a_closer_look) ImageView a_closer_look_imageView;
    @BindView(R.id.marketforce) ImageView marketforce_imageView;
    @BindView(R.id.alta) ImageView alta_imageView;

    public static CompaniesFragment newInstance() {

        Bundle args = new Bundle();

        CompaniesFragment fragment = new CompaniesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CompaniesFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_companies, container, false);
        ButterKnife.bind(this, mViewRoot);
        a_closer_look_imageView = (ImageView)mViewRoot.findViewById(R.id.a_closer_look);
        a_closer_look_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupWebView("http://a-closer-look.com");
            }
        });
        marketforce_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupWebView("http://www.marketforce.com/");
            }
        });
        alta_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupWebView("http://www.alta360research.com/");
            }
        });
        return mViewRoot;
    }

    private void setupWebView(String url) {
        new FinestWebView.Builder(getContext())
                .titleDefault("Mystery Shop")
                .webViewBuiltInZoomControls(true)
                .webViewDisplayZoomControls(true)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.view_slide_down, R.anim.activity_open_exit,
                        R.anim.activity_close_enter, R.anim.activity_close_exit)
                .show(url);
    }
}
