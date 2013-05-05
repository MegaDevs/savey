package com.megadevs.savey.machineclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.megadevs.savey.machinecommon.camera.CameraManager;
import com.megadevs.savey.machinecommon.camera.PreviewCallbackManager;
import com.megadevs.savey.machinecommon.data.QrCodeData;

public class CameraFragment extends BaseFragment implements PreviewCallbackManager.OnQrCodeReadedListener {

    public static CameraFragment getInstance() {
        return new CameraFragment();
    }

    private SurfaceView surface;
    private CameraManager cameraManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        surface = (SurfaceView) view.findViewById(R.id.surface);
        cameraManager = new CameraManager(getMainActivity(), surface.getHolder(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraManager.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cameraManager.release();
    }

    @Override
    public void onQrCodeReaded(QrCodeData data) {
        if (data != null) {
            getMainActivity().getRemoteTask(data, true);
        }
    }
}
