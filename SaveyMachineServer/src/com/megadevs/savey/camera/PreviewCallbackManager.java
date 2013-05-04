package com.megadevs.savey.camera;

import android.hardware.Camera;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.megadevs.savey.machineserver.Logg;
import com.megadevs.savey.machineserver.data.QrCodeData;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreviewCallbackManager implements Camera.PreviewCallback {

    private static AtomicBoolean LOCK = new AtomicBoolean(false);

    private static final long CAPTURE_TRESHOLD = TimeUnit.SECONDS.toMillis(30);

    private int previewWidth;
    private int previewHeight;
    private long lastQrCodeReadedTime = 0L;

    private OnQrCodeReadedListener listener;

    private Gson gson = new Gson();

    public PreviewCallbackManager(int previewWidth, int previewHeight, OnQrCodeReadedListener listener) {
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.listener = listener;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (LOCK.get()) {
            return;
        }
        long time = System.currentTimeMillis();
        if (time - lastQrCodeReadedTime > CAPTURE_TRESHOLD) {
            LOCK.set(true);
            try {
                Logg.v("Decoding frame...");
                LuminanceSource source = new PlanarYUVLuminanceSource(data, previewWidth, previewHeight, 0, 0, previewWidth, previewHeight, false);
                QRCodeReader reader = new QRCodeReader();
                Result decode = reader.decode(new BinaryBitmap(new HybridBinarizer(source)));
                String text = decode.getText();
                Logg.d("Decoded data: %s", text);
                if (listener != null) {
                    listener.onQrCodeReaded(gson.fromJson(text, QrCodeData.class));
                }
                lastQrCodeReadedTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LOCK.set(false);
            }
        }
    }

    public interface OnQrCodeReadedListener {
        public void onQrCodeReaded(QrCodeData data);
    }

}
