package com.megadevs.savey.machineserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.savey.machinecommon.camera.CameraManager;
import com.megadevs.savey.machinecommon.camera.PreviewCallbackManager;
import com.megadevs.savey.machinecommon.data.APIResponse;
import com.megadevs.savey.machinecommon.data.QrCodeData;
import com.megadevs.savey.machinecommon.network.RealWebService;
import com.megadevs.savey.machinecommon.network.WebService;

public class MainActivity extends Activity {

    private WebService webService = RealWebService.getInstance();

    private SurfaceView surface;

    private CameraManager cameraManager;
    private PreviewCallbackManager.OnQrCodeReadedListener onQrCodeReadedListener = new PreviewCallbackManager.OnQrCodeReadedListener() {
        @Override
        public void onQrCodeReaded(final QrCodeData data) {
            try {
                Logg.d("Checking task...");
                int taskId = Integer.valueOf(data.savey);
                webService.getCredit(taskId, new WebService.OnWebServiceResponse() {
                    @Override
                    public void onWebServiceResponse(APIResponse response) {
                        //TODO manage
                    }
                });
            } catch (Exception e) {
                Logg.e("Error while checking task: %s", e.getMessage());
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Logg.setTag("SaveyServerMachine");

        startService(new Intent(this, SocketService.class));

        surface = (SurfaceView) findViewById(R.id.surface);
        cameraManager = new CameraManager(this, surface.getHolder(), onQrCodeReadedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.onPause();
    }

}
