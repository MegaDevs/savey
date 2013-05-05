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
                        getRemoteTask(GsonWrapper.getQrCodeData(content));
                    }
                } catch (Exception e) {
                    Logg.e("Error while reading tag: %s", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void getRemoteTask(QrCodeData data) {
        if (data != null) {
            RealWebService.getInstance().getTask(Integer.valueOf(data.savey), User.getInstance().getId(), new WebService.OnWebServiceResponse() {
                @Override
                public void onWebServiceResponse(APIResponse response) {
                    if (response != null) {
                        showTask(response);
                    }
                }
            });
        }
    }

    private void showTask(final APIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideFragment(FragmentTag.CAMERA);
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
        hideFragment(FragmentTag.TASK);
        QrCodeFragment frag = QrCodeFragment.getInstance(response.qrcode);
    }

    private void showFragment(Fragment fragment, FragmentTag tag) {
        showFragment(fragment, tag, true);
    }

    private void showFragment(Fragment fragment, FragmentTag tag, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, tag.name());
        if (addToBackStack) {
            transaction.addToBackStack(tag.name());
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragment(FragmentTag tag) {
        Fragment fragment = getFragmentByTag(tag.name());
        if (fragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    private Fragment getFragmentByTag(String tag) {
        return getFragmentManager().findFragmentByTag(tag);
    }

    public enum FragmentTag {
        MAIN,
        TASK,
        CAMERA,
        QRCODE
    }

}
