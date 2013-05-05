package com.megadevs.tagutilslib.test;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.megadevs.tagutilslib.R;
import com.megadevs.tagutilslib.TULReader;


public class TestAct extends Activity {
	
	private TextView txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		txt = (TextView) findViewById(R.id.txt);
		findViewById(R.id.btn_write).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestAct.this, WriteAct.class);
				startActivity(intent);
			}
		});
		
		manageIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		manageIntent(intent);
	}
	
	private void manageIntent(Intent intent) {
		try {
			Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
			if (tag != null) {
				String msg = TULReader.readStrings(tag)[0];
				txt.append(msg);
				txt.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
