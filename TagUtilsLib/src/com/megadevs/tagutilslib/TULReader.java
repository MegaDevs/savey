package com.megadevs.tagutilslib;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

/**
 * Utility class to read NFC tags
 * @author Dario Marcato
 *
 */
public class TULReader {
	
	/**
	 * Exception launched when we can't read the tag
	 * @author dario
	 *
	 */
	public static class NotReadableException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -653287114114210831L;

		public NotReadableException(String msg) {
			super(msg);
		}
	}

	public static String[] readStrings(Tag tag) throws IOException, FormatException, NotReadableException {
		Ndef ndefTag = Ndef.get(tag);
		if (ndefTag == null) {
			throw new NotReadableException("The tag doesn't support Ndef tech");
		}
		ndefTag.connect();
		NdefMessage message = ndefTag.getNdefMessage();
		NdefRecord[] records = message.getRecords();
		String[] out = new String[records.length];
		int i = 0;
		for (NdefRecord rec : records) {
			out[i++] = new String(rec.getPayload(), Charset.forName("UTF-8"));
		}
		ndefTag.close();
		return out;
	}
	
	public static NdefRecord[] readRecords(Tag tag) throws IOException, FormatException, NotReadableException {
		Ndef ndefTag = Ndef.get(tag);
		if (ndefTag == null) {
			throw new NotReadableException("The tag doesn't support Ndef tech");
		}
		ndefTag.connect();
		NdefMessage message = ndefTag.getNdefMessage();
		NdefRecord[] records = message.getRecords();
		ndefTag.close();
		return records;
	}

}
