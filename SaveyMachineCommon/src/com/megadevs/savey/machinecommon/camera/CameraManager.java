package com.megadevs.savey.machinecommon.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.megadevs.savey.machinecommon.Logg;

import java.io.IOException;

public class CameraManager {

    private Camera camera;
    private SurfaceHolder holder;
    private boolean configured = false;
    private boolean previewRunning = false;

    private int previewWidth;
    private int previewHeight;

    private PreviewCallbackManager previewCallbackManager;
    private AutofocusThread autofocusThread;

    public CameraManager(final Activity activity, final SurfaceHolder holder, final PreviewCallbackManager.OnQrCodeReadedListener onQrCodeReadedListener) {
        this(activity, holder, onQrCodeReadedListener, true);
    }

    public CameraManager(final Activity activity, final SurfaceHolder holder, final PreviewCallbackManager.OnQrCodeReadedListener onQrCodeReadedListener, final boolean autostart) {
        camera = Camera.open();
        this.holder = holder;
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    setCameraDisplayOrientation(activity);
                    camera.setPreviewDisplay(holder);
                    Camera.Parameters parameters = camera.getParameters();
                    camera.setParameters(parameters);
                    previewWidth = camera.getParameters().getPreviewSize().width;
                    previewHeight = camera.getParameters().getPreviewSize().height;
                    previewCallbackManager = new PreviewCallbackManager(previewWidth, previewHeight, onQrCodeReadedListener);
                    configured = true;
                    if (autostart) {
                        startPreview();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });
    }

    public void onResume() {
        startPreview();
    }

    public void onPause() {
        stopPreview();
    }

    public void release() {
        if (camera != null) {
            camera.release();
        }
    }

    private void startPreview() {
        if (camera != null && !previewRunning && configured) {
            if (previewCallbackManager != null) {
                camera.setPreviewCallback(previewCallbackManager);
            }
            camera.startPreview();
            previewRunning = true;
            if (autofocusThread != null) {
                autofocusThread.interrupt();
            }
            autofocusThread = new AutofocusThread();
            autofocusThread.start();
        }
    }

    private void stopPreview() {
        if (camera != null && previewRunning) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            previewRunning = false;
            if (autofocusThread != null) {
                autofocusThread.interrupt();
                autofocusThread = null;
            }
        }
    }

    public void setCameraDisplayOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private class AutofocusThread extends Thread {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(3000);
                    try {
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                Logg.v("Autofocus result: %s", success);
                            }
                        });
                    } catch (Exception e) {}
                }
            } catch (InterruptedException e) {}
        }
    }

}
