package com.gorrilaport.mysteryshoptools.ui.notedetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.MysteryShopTools;
import com.gorrilaport.mysteryshoptools.ui.notelist.NoteListContract;
import com.gorrilaport.mysteryshoptools.util.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteImageView extends AppCompatActivity {

    @BindView(R.id.photoView) PhotoView mPhotoView;
    @Inject
    NoteListContract.Repository mRepository;

    private NoteDetailPresenter mPresenter;
    private String mImagePath;

    public NoteImageView(){
        MysteryShopTools.getInstance().getAppComponent().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            this.mPresenter = bundle.getParcelable(Constants.DETAIL_PRESENTER);

        if (getIntent().hasExtra(Constants.IMAGE_PATH)){
            String imagePath = getIntent().getStringExtra(Constants.IMAGE_PATH);
            populateImage(imagePath);
            mImagePath = imagePath;
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mRepository.deleteAsyncImage(mImagePath);
                onBackPressed();
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateImage(String profileImagePath) {
        mPhotoView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(profileImagePath)
                .into(mPhotoView);
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(velocityX >= 5000 || velocityX <= -5000 || velocityY >= 5000 || velocityY <= -5000){
                onBackPressed();
            }
            return true;
        }
    }
}
