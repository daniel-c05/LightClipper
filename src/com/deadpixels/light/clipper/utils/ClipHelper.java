package com.deadpixels.light.clipper.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import com.deadpixels.light.clipper.Home;

public class ClipHelper {
	
	public static final String FILE_CLIPS = "clips.txt";
	public static final String EMPTY_STRING = "";
	
	public static void clearAll(Context context) {
		try {
			FileOutputStream out = context.openFileOutput(FILE_CLIPS, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(0);
			dos.writeUTF(EMPTY_STRING);
			dos.flush();
			dos.close();
		} catch (Exception e) {
			Log.v(Home.TAG, e.toString());
		}
	}	
	
	public static void saveClips(Context context, ArrayList<String> recentClips) {
		
		try {
			FileOutputStream out = context.openFileOutput(FILE_CLIPS, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(recentClips.size());
			for (String clip: recentClips) {
				dos.writeUTF(clip);
			}
			dos.flush();
			dos.close();
		} catch (Exception e) {
			Log.v(Home.TAG, e.toString());
		}
	}
	
	public static ArrayList<String> getSavedClips(Context context) {
		
		ArrayList<String> recentClips = new ArrayList<String>();
		
		try {
			FileInputStream in = context.openFileInput(FILE_CLIPS);
			DataInputStream dis = new DataInputStream(in);
			int size = dis.readInt();
			for (int i = 0; i < size; i++) {
				String line = dis.readUTF();
				recentClips.add(line);
			}
			dis.close();
		} catch (Exception e) {
			Log.v(Home.TAG, e.toString());
		}
		
		return recentClips;
		
	}
	
	public static void addItemToClipboard(Context context, String label, String value, boolean oldAPI) {
		if (oldAPI) {
			addTextToClipboard(context, label, value);
		}
		else {
			addItemToClipboard(context, label, value);
		}
	}
	
	@SuppressLint("NewApi")
	public static void addItemToClipboard (Context context, String label, String value) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData.Item item = new Item(value);
		ClipDescription description = new ClipDescription(label, new String [] {ClipDescription.MIMETYPE_TEXT_PLAIN});
		ClipData data = new ClipData(description, item);
		manager.setPrimaryClip(data);				
	}
	
	@SuppressWarnings("deprecation")
	public static void addTextToClipboard (Context context, String label, String value) {
		android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		manager.setText(value);			
	}
	
	@SuppressLint("NewApi")
	public static String getItemFromCliboard (Context context) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		final ClipData data = manager.getPrimaryClip();
		
		if (data == null) {
			return EMPTY_STRING;
		}		
		
		final String clipItem;
		
		if (data.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
			ClipData.Item item = data.getItemAt(0);
			clipItem = item.getText().toString();
		}
		
		else {
			clipItem = EMPTY_STRING;
		}
		
		return clipItem;
		
	}
	
	@SuppressWarnings("deprecation")
	private static String getTextFromClipboard(Context context) {
		android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		String text = (String) manager.getText();
		if (text == null) {
			text = EMPTY_STRING;
		}
		return text;
	}
	
	public static String getLastClip (Context context, boolean oldAPI) {		
		
		final String clip;
		
		if (oldAPI) {
			clip = getTextFromClipboard(context);
		}
		else {
			clip = getItemFromCliboard(context);
		}		
		
		return clip;
		
	}	

}
