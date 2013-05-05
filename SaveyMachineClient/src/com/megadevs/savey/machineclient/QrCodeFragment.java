package com.megadevs.savey.machineclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.megadevs.savey.machinecommon.Logg;

public class QrCodeFragment extends BaseFragment {

    public static final String EXTRA_QRCODE = "qrcode";

    public static QrCodeFragment getInstance(String qrcode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_QRCODE, qrcode);
        QrCodeFragment frag = new QrCodeFragment();
        frag.setArguments(args);
        return frag;
    }

    private ImageView imageQrCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageQrCode = (ImageView) view.findViewById(R.id.image_qrcode);
        showQrCode(getArguments().getString(EXTRA_QRCODE, null));
    }

    private void showQrCode(final String data) {
        if (data == null) {
            //TODO manage null data
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    Logg.d("Trying to decode image: %s", data);
                    byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
                    getMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageQrCode.setImageBitmap(bitmap);
                            imageQrCode.startAnimation(AnimationUtils.loadAnimation(getMainActivity(), android.R.anim.fade_in));
                        }
                    });
                    Logg.d("Image decoded");
                } catch (Exception e) {
                    Logg.e("Couldn't show qrcode: %s", e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
