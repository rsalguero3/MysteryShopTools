package com.gorrilaport.mysteryshoptools;


import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Ricardo on 3/6/2016.
 */
public class CameraFragment extends SingleFragment {
    private Button mReceiptButton, mCameraButton;
    private ImageView mImageView;
    private final static int RECEIPT_RESULT_ID = 0;

    @Override
    public void onActivityCreated() {
        mReceiptButton = (Button)getView().findViewById(R.id.camera_fragment_receipt_button);
        mCameraButton = (Button) getView().findViewById(R.id.camera_fragment_image_button);
        mImageView = (ImageView)getView().findViewById(R.id.imageView);

        mReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cameraIntent);
            }
        });
    }
}
