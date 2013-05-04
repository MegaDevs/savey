package com.megadevs.savey.machineclient;

import android.app.Activity;
import android.app.Fragment;

public class BaseFragment extends Fragment {

    private MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mainActivity = (MainActivity) activity;
        } else {
            throw new IllegalStateException("Activity must be MainActivity");
        }
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }
}
