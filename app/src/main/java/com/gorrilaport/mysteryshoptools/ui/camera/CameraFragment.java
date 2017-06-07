package com.gorrilaport.mysteryshoptools.ui.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.gorrilaport.mysteryshoptools.R;

public class CameraFragment extends Fragment implements View.OnClickListener{
    private Toolbar mToolbar;
    private Button mReceiptButton, mCameraButton;
    private ImageView mImageView;
    private final static int RECEIPT_REQUEST_ID = 0;
    public Activity mActivity;

    public static CameraFragment newInstance(){ return new CameraFragment();}

    public CameraFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

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
        if (v.getId() == R.id.camera_fragment_image_button){
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //noinspection unchecked,unchecked
            startActivityForResult(cameraIntent, RECEIPT_REQUEST_ID);
        }
        else {
            Toast.makeText(getContext(), "Generating PDF", Toast.LENGTH_SHORT).show();
        }

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_drawer, menu);
//        inflater.inflate(R.menu.camera_fragment_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}
