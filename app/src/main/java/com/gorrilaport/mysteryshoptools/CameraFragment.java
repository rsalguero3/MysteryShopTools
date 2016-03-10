package com.gorrilaport.mysteryshoptools;


import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Ricardo on 3/6/2016.
 */
public class CameraFragment extends SingleFragment implements View.OnClickListener{
    private Button mReceiptButton, mCameraButton;
    private ImageView mImageView;
    private final static int RECEIPT_RESULT_ID = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReceiptButton = (Button)getActivity().findViewById(R.id.camera_fragment_receipt_button);
        mCameraButton = (Button)getActivity().findViewById(R.id.camera_fragment_image_button);
        mImageView = (ImageView)getActivity().findViewById(R.id.imageView);

        mReceiptButton.setOnClickListener(this);
        mCameraButton.setOnClickListener(this);
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
            startActivityForResult(cameraIntent, RECEIPT_RESULT_ID);
        }
        else {
            Toast.makeText(getContext(), "meow", Toast.LENGTH_SHORT).show();
        }

    }
}
