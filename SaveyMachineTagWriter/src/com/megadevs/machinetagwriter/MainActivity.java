package com.megadevs.machinetagwriter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.tagutilslib.TULWriter;

public class MainActivity extends Activity {

    private NfcAdapter nfcAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Logg.setTag("SaveyMachineClient");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] {filter}, null);

        manageIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        manageIntent(intent);
    }

    private void manageIntent(Intent intent) {
        try {
            Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                NdefRecord dataRecord = TULWriter.createMimeRecord("application/com.megadevs.savey.nfc", "{\"savey\":\"1\"}");
                TULWriter.write(tag, dataRecord);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
