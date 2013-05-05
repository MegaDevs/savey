package com.megadevs.savey.machineserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.savey.machinecommon.camera.CameraManager;
import com.megadevs.savey.machinecommon.camera.PreviewCallbackManager;
import com.megadevs.savey.machinecommon.data.APIResponse;
import com.megadevs.savey.machinecommon.data.QrCodeData;
import com.megadevs.savey.machinecommon.network.RealWebService;
import com.megadevs.savey.machinecommon.network.WebService;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private WebService webService = RealWebService.getInstance();

    private SurfaceView surface;
    private Button btnRestartService;
    private TextView txtLog;

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
                        if (response != null) {
                            if (response.valid) {
                                Logg.d("Adding credit: %f", response.credit);
                                ArduinoHandler.getInstance().addCredit(response.credit);
                            } else {
                                Logg.e("Task is expired");
                            }
                        } else {
                            Logg.e("Error while reading response");
                        }
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

        ArduinoHandler.getInstance().onCreate(this, savedInstanceState);

        startService(new Intent(this, SocketService.class));

        btnRestartService = (Button) findViewById(R.id.btn_restart_service);
        btnRestartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Class klass = Class.forName("android.os.Process");
                    Method killProcess = klass.getDeclaredMethod("killProcess", int.class);
                    Method myPid = klass.getDeclaredMethod("myPid");
                    Integer pid = (Integer) myPid.invoke(null);
                    killProcess.invoke(null, pid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        txtLog = (TextView) findViewById(R.id.log);

        surface = (SurfaceView) findViewById(R.id.surface);
        cameraManager = new CameraManager(this, surface.getHolder(), onQrCodeReadedListener);
        MusicPlayer.getInstance().init(this);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return ArduinoHandler.getInstance().onRetainNonConfigurationInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArduinoHandler.getInstance().onResume();
        cameraManager.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ArduinoHandler.getInstance().onPause();
        cameraManager.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        ArduinoHandler.getInstance().onDestroy(this);
        cameraManager.release();
        MusicPlayer.getInstance().destroy();
        super.onDestroy();
    }
}
