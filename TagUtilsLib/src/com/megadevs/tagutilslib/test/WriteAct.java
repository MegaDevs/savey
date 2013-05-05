package com.megadevs.tagutilslib.test;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.EditText;

import com.megadevs.tagutilslib.R;
import com.megadevs.tagutilslib.TULWriter;

public class WriteAct extends Activity {

	private EditText edit;
	private NfcAdapter nfcAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write);
		edit = (EditText) findViewById(R.id.edit);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent(this, WriteAct.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] {filter}, null);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		try {
			Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
			if (tag != null) {
				NdefRecord appRecord = TULWriter.createApplicationRecord(getPackageName());
//				NdefRecord dataRecord = TULWriter.createTextRecord(edit.getText().toString(), Locale.ITALY, true);
				NdefRecord dataRecord = TULWriter.createMimeRecord("application/com.megadevs.tul", edit.getText().toString());
				TULWriter.write(tag, dataRecord, appRecord);
//				
//				NdefMessage message = new NdefMessage(new NdefRecord[] {
//						dataRecord,
//						appRecord
//				});
//				
//				Ndef ndefTag = Ndef.get(tag);
//				ndefTag.connect();
//				ndefTag.writeNdefMessage(message);
//				ndefTag.close();
				
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
