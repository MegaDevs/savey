package com.megadevs.savey.machineserver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.megadevs.savey.machinecommon.Logg;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArduinoHandler implements Runnable {

	private static final String TAG = "TestArduino";
	private static final String ACTION_USB_PERMISSION = "test.arduino.action.USB_PERMISSION";

    private static ArduinoHandler instance;

    public static ArduinoHandler getInstance() {
        if (instance == null) {
            instance = new ArduinoHandler();
        }
        return instance;
    }

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

	private UsbAccessory mAccessory;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;

	int dummy = 25;

	private BroadcastReceiver mUsbReceiver;

    private double currentCredit = 0.0;

	private ArduinoHandler(){
		mUsbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (ACTION_USB_PERMISSION.equals(action)) {
					synchronized (this) {
						//UsbAccessory accessory = UsbManager.getAccessory(intent);
						UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
						if (intent.getBooleanExtra(
								UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							openAccessory(accessory);
						} else {
							Log.d(TAG, "permission denied for accessory "
									+ accessory);
						}
						mPermissionRequestPending = false;
					}
				} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
					//UsbAccessory accessory = UsbManager.getAccessory(intent);
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (accessory != null && accessory.equals(mAccessory)) {
						closeAccessory();
					}
				}
			}
		};	

	}

	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "TestArduino");
			thread.start();
			Log.d(TAG, "accessory opened");
			//enableControls(true);
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		//enableControls(false);

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

    public double getCurrentCredit() {
        return currentCredit;
    }

    public void addCredit(double credit) {
        currentCredit += credit;
        if (currentCredit > 9.99) {
            currentCredit = 9.99;
        }
        updateCurrentCredit();
    }

    public void removeCredit(double credit) {
        currentCredit -= credit;
        if (currentCredit < 0.00) {
            currentCredit = 0.00;
        }
        updateCurrentCredit();
    }

    private void updateCurrentCredit() {
        int[] digits = double2ints(currentCredit);
        Logg.d("Current credit: %f", currentCredit);
        updatePrice(digits[0], digits[1], digits[2]);
    }

    public int[] double2ints(double value) {
        String creditString = "" + currentCredit;
        Logg.d("CreditString: %s", creditString);
        int[] out = new int[3];
        out[0] = Integer.valueOf("" + creditString.charAt(0));
        out[1] = Integer.valueOf("" + creditString.charAt(2));
        out[2] = (creditString.length() >= 4) ? Integer.valueOf("" + creditString.charAt(3)) : 0;
        return out;
    }

    public void resetCredit() {
        currentCredit = 0.0;
        updatePrice(0, 0, 0);
    }

	public void updatePrice(final int a, final int b, final int c){
        Logg.d("Updating price with: %d, %d, %d", a, b, c);
		Thread t = new Thread(){
			public void run(){
				sendCommand(a, b, c, dummy);
			}
		};
		t.start();
	}

	public void startCoffe(final int secTotDuration){
		Thread t = new Thread(){
			public void run(){
				int step = secTotDuration*1000/8;

				int c = 0;
				while(c < 7){
					sendCommand(dummy,dummy,dummy, c);
					c++;
					try {
						Thread.sleep(step);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
		t.start();
	}


	public void run() {

		//TODO test display async
//		Thread t=new Thread(){
//			public void run(){
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				sendCommand(1,2,3,dummy);
//			}
//		};
//		t.start();

		//TODO test progress
//		startCoffe(10);

		//TODO test display
//		sendCommand(9,9,9,dummy);


		//Keep up...
		byte[] buffer = new byte[64];
		int ret=0;
		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}
		}
	}



	private void sendCommand(int command, int target, int value, int progress) {
		byte[] buffer = new byte[4];

		buffer[0] = (byte)command;
		buffer[1] = (byte)target;
		buffer[2] = (byte)value;
		buffer[3] = (byte)progress;
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
	}


	public void onPause(){
		closeAccessory();
	}

	public void onDestroy(Context context) {
		context.unregisterReceiver(mUsbReceiver);
	}

	public Object onRetainNonConfigurationInstance(Activity activity) {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return activity.onRetainNonConfigurationInstance();
		}
	}

	public void onCreate(Activity activity, Bundle savedInstanceState) {
		//mUsbManager = UsbManager.getInstance(this);
		mUsbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);

		mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		activity.registerReceiver(mUsbReceiver, filter);

		if (activity.getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) activity.getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}

		//setContentView(R.layout.main);
		//testo = (TextView)findViewById(R.id.testo);
	}
	
	public void onResume() {
		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}

}
