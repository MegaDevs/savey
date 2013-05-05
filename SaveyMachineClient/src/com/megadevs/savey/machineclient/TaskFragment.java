package com.megadevs.savey.machineclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.savey.machinecommon.data.APIResponse;

public class TaskFragment extends BaseFragment {

    public static final String EXTRA_API_RESPONSE = "api_response";

    public static TaskFragment getInstance(APIResponse response) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_API_RESPONSE, response);
        TaskFragment frag = new TaskFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle data = getArguments();
        if (data == null) {
            Logg.e("Arguments can't be null");
            throw new IllegalStateException("Arguments can't be null");
        }
        APIResponse response = (APIResponse) data.getSerializable(EXTRA_API_RESPONSE);
    }

}
