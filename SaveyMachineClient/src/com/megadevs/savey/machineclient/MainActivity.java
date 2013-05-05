package com.megadevs.savey.machineclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import com.megadevs.savey.machinecommon.GsonWrapper;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.savey.machinecommon.data.APIResponse;
import com.megadevs.savey.machinecommon.data.QrCodeData;
import com.megadevs.savey.machinecommon.network.RealWebService;
import com.megadevs.savey.machinecommon.network.WebService;
import com.megadevs.tagutilslib.TULReader;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Logg.setTag("SaveyMachineClient");

        MainFragment frag = MainFragment.getInstance();
        showFragment(frag, FragmentTag.MAIN, false);

        manageIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        manageIntent(intent);
    }

    private void manageIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                try {
                    String content = TULReader.readStrings(tag)[0];
                    Logg.d("Tag content: %s", content);
                    if (content != null) {
                        getRemoteTask(GsonWrapper.getQrCodeData(content), false);
                    }
                } catch (Exception e) {
                    Logg.e("Error while reading tag: %s", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment frag = getFragmentManager().findFragmentById(R.id.container);
        switch (FragmentTag.valueOf(frag.getTag())) {
            case QRCODE:
            case TASK:
                showFragment(MainFragment.getInstance(), FragmentTag.MAIN, false);
                break;
        }
    }

    public void getRemoteTask(QrCodeData data, final boolean comingFromCamera) {
        if (data != null) {
            showLoadingFragment(false);
            RealWebService.getInstance().getTask(Integer.valueOf(data.savey), User.getInstance().getId(), new WebService.OnWebServiceResponse() {
                @Override
                public void onWebServiceResponse(APIResponse response) {
                    if (response != null) {
                        showTask(response, comingFromCamera);
                    }
                }
            });
        }
    }

    private void showTask(final APIResponse response, final boolean comingFromCamera) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (comingFromCamera) {
                    popBackStack();
                }
                TaskFragment frag = TaskFragment.getInstance(response);
                showFragment(frag, FragmentTag.TASK);
            }
        });
    }

    public void showCameraCapture() {
        CameraFragment frag = CameraFragment.getInstance();
        showFragment(frag, FragmentTag.CAMERA);
    }

    public void showTaskQrCode(APIResponse response) {
        popBackStack();
        QrCodeFragment frag = QrCodeFragment.getInstance(response.qr_code);
        showFragment(frag, FragmentTag.QRCODE);
    }

    public void showLoadingFragment(boolean popBackStack) {
        if (popBackStack) {
            popBackStack();
        }
        LoadingFragment frag = LoadingFragment.getInstance();
        showFragment(frag, FragmentTag.LOADING, false);
    }

    private void popBackStack() {
        getFragmentManager().popBackStackImmediate();
    }

    private void showFragment(Fragment fragment, FragmentTag tag) {
        showFragment(fragment, tag, true, true);
    }

    private void showFragment(Fragment fragment, FragmentTag tag, boolean addToBackStack) {
        showFragment(fragment, tag, addToBackStack, true);
    }

    private void showFragment(Fragment fragment, FragmentTag tag, boolean addToBackStack, boolean ignoreCheck) {
        if (tag != FragmentTag.LOADING) {
            hideFragment(FragmentTag.LOADING, ignoreCheck);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, tag.name());
        if (addToBackStack) {
            transaction.addToBackStack(tag.name());
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
    }

    private void hideFragment(FragmentTag tag) {
        hideFragment(tag, true);
    }

    private void hideFragment(FragmentTag tag, boolean ignoreCheck) {
        Fragment fragment = getFragmentByTag(tag.name());
        if (fragment != null) {
            //if (!getFragmentManager().popBackStackImmediate(tag.name(), FragmentManager.POP_BACK_STACK_INCLUSIVE)) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commitAllowingStateLoss();
            //}
        }
        if (!ignoreCheck) {
            checkNoFragmentLoaded();
        }
    }

    private void checkNoFragmentLoaded() {
        for (FragmentTag tag : FragmentTag.values()) {
            if (isFragmentAdded(tag)) {
                return;
            }
        }
        showFragment(MainFragment.getInstance(), FragmentTag.MAIN, false, false);
    }

    private Fragment getFragmentByTag(String tag) {
        return getFragmentManager().findFragmentByTag(tag);
    }

    private boolean isFragmentAdded(FragmentTag tag) {
        return getFragmentByTag(tag.name()) != null;
    }

    public enum FragmentTag {
        MAIN,
        TASK,
        CAMERA,
        QRCODE,
        LOADING
    }

}
