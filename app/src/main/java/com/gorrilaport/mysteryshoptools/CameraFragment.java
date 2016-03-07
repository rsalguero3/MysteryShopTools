package com.gorrilaport.mysteryshoptools;


import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;

/**
 * Created by Ricardo on 3/6/2016.
 */
public class CameraFragment extends SingleFragment {
    CameraManager manager;

    public void CameraManager() {
        try {
            manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            String[] camIds = manager.getCameraIdList();
        } catch (Exception e) {
            Context context = MainActivity.getApplicationContext();
            Toast.makeText(MainActivity.getApplicationContext(), "", Toast.LENGTH_SHORT).show();

        }

        android.os.Handler handler = new android.os.Handler();

        CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {

            }

            @Override
            public void onDisconnected(CameraDevice camera) {

            }

            @Override
            public void onError(CameraDevice camera, int error) {

            }
        };

        try {
            manager.openCamera("1", mStateCallback, handler);
        } catch (CameraAccessException e) {

        } catch (SecurityException e) {

        }

    }
}
