package com.megadevs.savey.machineclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends BaseFragment {

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    private Button btnShowCamera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnShowCamera = (Button) view.findViewById(R.id.btn_show_camera);

        btnShowCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraCapture();
            }
        });
    }

    private void showCameraCapture() {
        getMainActivity().showCameraCapture();
    }

}
