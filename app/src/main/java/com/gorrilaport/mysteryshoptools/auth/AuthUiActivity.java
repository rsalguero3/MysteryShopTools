/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gorrilaport.mysteryshoptools.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListActivity;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.idescout.sql.SqlScoutServer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.firebase.ui.auth.ui.ExtraConstants.EXTRA_IDP_RESPONSE;

public class AuthUiActivity extends AppCompatActivity {
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL = "https://www.firebase.com/terms/terms-of-service.html";
    private static final int RC_SIGN_IN = 100;

    @BindView(R.id.sign_in)
    Button mSignIn;

    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.authUiLinearlayout)
    LinearLayout linearLayout;

    @BindView(R.id.link_login)
    TextView mLoginTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Force layout to take full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        MysteryShopTools.getInstance().getAppComponent().inject(this);

        //First time run is false show Notelist Activity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println(sharedPreferences.getBoolean(Constants.FIRST_RUN, false));
        if (!sharedPreferences.getBoolean(Constants.FIRST_RUN, false)
                && !getIntent().hasExtra(Constants.SIGN_IN_INTENT)) {
            startDefaultActivity();
        }

        setContentView(R.layout.auth_ui_layout);
        ButterKnife.bind(this);
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDefaultActivity();
            }
        });
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignInScreen();
            }
        });

        //Background gradient animation
        AnimationDrawable animationDrawable =(AnimationDrawable)linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }

    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.FullscreenTheme)
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setIsSmartLockEnabled(true)
                        .build(),
                RC_SIGN_IN);
    }

    private void showSignInScreen() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.FullscreenTheme)
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent in = new Intent(this, NoteListActivity.class);
            in.putExtra(EXTRA_IDP_RESPONSE, IdpResponse.fromResultIntent(data));
            in.putExtra(Constants.LOGED_IN, 200 );
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar(R.string.sign_in_cancelled);
            return;
        }

        if (IdpResponse.fromResultIntent(data).getErrorCode() == ErrorCodes.NO_NETWORK) {
            showSnackbar(R.string.no_internet_connection);
            return;
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    private List<IdpConfig> getSelectedProviders() {
        List<IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(new IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        selectedProviders.add(
                new IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());


        return selectedProviders;
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }



    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        result.add(Scopes.DRIVE_FILE);
        return result;
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthUiActivity.class);
        return in;
    }

    public void startDefaultActivity(){
        startActivity(new Intent(this, NoteListActivity.class));
        finish();
    }
}
