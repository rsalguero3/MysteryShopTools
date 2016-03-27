package com.gorrilaport.mysteryshoptools;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Ricardo on 3/6/2016.
 */
public class CameraFragment extends SingleFragment implements View.OnClickListener{
    private Toolbar mToolbar;
    private Button mReceiptButton, mCameraButton;
    private ImageView mImageView;
    private final static int RECEIPT_REQUEST_ID = 0;
    public Activity mActivity;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReceiptButton = (Button)getActivity().findViewById(R.id.camera_fragment_receipt_button);
        mCameraButton = (Button)getActivity().findViewById(R.id.camera_fragment_image_button);
        mImageView = (ImageView)getActivity().findViewById(R.id.imageView);

        mReceiptButton.setOnClickListener(this);
        mCameraButton.setOnClickListener(this);

        setHasOptionsMenu(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == -1) {
            Bundle extras = data.getExtras();
            Bitmap bp = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(bp);

        }
        else if(resultCode == 0){
            Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }
        else{

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_fragment_receipt_button){
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, RECEIPT_REQUEST_ID, ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
        }
        else {
            Toast.makeText(getContext(), "meow", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_drawer, menu);
        inflater.inflate(R.menu.camera_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
