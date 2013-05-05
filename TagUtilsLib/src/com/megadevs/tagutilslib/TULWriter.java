package com.megadevs.tagutilslib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

/**
 * Utility class to write NFC tags
 * @author Dario Marcato
 *
 */
public class TULWriter {
	
	/**
	 * Exception thrown when trying to format a non-formatable tag
	 * @author Dario Marcato
	 *
	 */
	public static class NotFormatableException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5680804388867427852L;

		public NotFormatableException(String msg) {
			super(msg);
		}
	}

	/**
	 * Format a given NFC tag
	 * @param tag The tag to format
	 * @throws NotFormatableException
	 * @throws IOException
	 * @throws FormatException 
	 */
	public static void format(Tag tag, NdefMessage message) throws NotFormatableException, IOException, FormatException {
		NdefFormatable formatable = NdefFormatable.get(tag);
		if (formatable == null) {
			throw new NotFormatableException("Tag doesn't support NdefFormatable");
		}
		formatable.connect();
		formatable.format(message);
		formatable.close();
	}
	
	/**
	 * Write records in tag
	 * @param tag The tag to write
	 * @param records The records to write
	 * @throws IOException
	 * @throws FormatException
	 * @throws NotFormatableException
	 */
	public static void write(Tag tag, NdefRecord... records) throws IOException, FormatException, NotFormatableException {
		NdefMessage message = new NdefMessage(records);
		Ndef ndefTag = Ndef.get(tag);
		if (ndefTag == null) {
			format(tag, message);
		} else {
			ndefTag.connect();
			ndefTag.writeNdefMessage(message);
			ndefTag.close();
		}
	}
	
	@SuppressLint("NewApi")
	/**
	 * Create a record to start a specific app (or install it if not present)
	 * @param packageName
	 * @return NdefRecord
	 */
	public static NdefRecord createApplicationRecord(String packageName) {
		return NdefRecord.createApplicationRecord(packageName);
	}
	
	@SuppressLint("NewApi")
	/**
	 * Create a mime record with additional payload data
	 * @param mime
	 * @param data
	 * @return NdefRecord
	 */
	public static NdefRecord createMimeRecord(String mime, String data) {
//		NdefRecord mimeRecord = NdefRecord.createMime("application/vnd.com.example.android.beam",
//			    "Beam me up, Android".getBytes(Charset.forName("US-ASCII")));
		return NdefRecord.createMime(mime, data.getBytes(Charset.forName("UTF-8")));
	}
	
	/**
	 * Create a text record
	 * @param payload
	 * @param locale
	 * @param encodeInUtf8
	 * @return NdefRecord
	 */
	public static NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
	    byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
	    Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
	    byte[] textBytes = payload.getBytes(utfEncoding);
	    int utfBit = encodeInUtf8 ? 0 : (1 << 7);
	    char status = (char) (utfBit + langBytes.length);
	    byte[] data = new byte[1 + langBytes.length + textBytes.length];
	    data[0] = (byte) status;
	    System.arraycopy(langBytes, 0, data, 1, langBytes.length);
	    System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
	    NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
	    NdefRecord.RTD_TEXT, new byte[0], data);
	    return record;
	}

}
