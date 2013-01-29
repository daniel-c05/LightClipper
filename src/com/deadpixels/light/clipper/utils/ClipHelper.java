/*******************************************************************************
 * Copyright 2007 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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

/**
 * Helper methods to get, set, save locally, and restore clipboard items. 
 * @author Daniel Alvarado
 *
 */
public class ClipHelper {
	
	public static final String FILE_CLIPS = "clips.txt";
	public static final String EMPTY_STRING = "";
	
	/**
	 * 
	 * @param context The context, required to call {@code openFileOutput}
	 * @param recentClips The actual list of String items we want to write to the file. 
	 */
	public static void saveClips(final Context context, final ArrayList<String> recentClips) {
		
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
	
	/**
	 * 
	 * @param context The context, required to call {@code openFileInput}
	 * @return A list of the clips that were saved via {@link #saveClips(Context, ArrayList)}
	 */
	public static ArrayList<String> getSavedClips(final Context context) {
		
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
	
	/**
	 * 
	 * @param context The context
	 * @param label	The label to show to the user via {@code ClipDescription}
	 * @param value	The actual value to store on the clipboard via {@link ClipHelper#addItemToClipboard(Context, String, String)}
	 * @param oldAPI Whether or not we are running on pre-HoneyComb API.
	 */
	public static void addItemToClipboard(final Context context, final String label, final String value, final boolean oldAPI) {
		if (oldAPI) {
			addTextToClipboard(context, value);
		}
		else {
			addItemToClipboard(context, label, value);
		}
	}
	
	/**
	 * This is only called when oldAPi is passed as false on {@link #addItemToClipboard(Context, String, String, boolean)}
	 * @param context The context, required to get the Cliboard System Service. 
	 * @param label The label to show to the user via {@code ClipDescription}
	 * @param value The value to store on the clipboard. 
	 */
	@SuppressLint("NewApi")
	private static void addItemToClipboard (final Context context, final String label, final String value) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData.Item item = new Item(value);
		ClipDescription description = new ClipDescription(label, new String [] {ClipDescription.MIMETYPE_TEXT_PLAIN});
		ClipData data = new ClipData(description, item);
		manager.setPrimaryClip(data);				
	}
	
	/**
	 * This is only called when oldAPi is passed as true on {@link #addItemToClipboard(Context, String, String, boolean)}
	 * @param context The context, required to get the Cliboard System Service. 
	 * @param value The value to store on the clipboard. 
	 */
	@SuppressWarnings("deprecation")
	private static void addTextToClipboard (final Context context, final String value) {
		android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		manager.setText(value);			
	}
	
	/**
	 * This is only called when we are on API level > Honeycomb
	 * @param context The context, required to get the Cliboard System Service. 
	 * @return Returns the latest item added to the clipboard by the user, as long as the {@code MimeType} is {@code MIMETYPE_TEXT_PLAIN}
	 */
	@SuppressLint("NewApi")
	public static String getItemFromCliboard (final Context context) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		final ClipData data = manager.getPrimaryClip();
		
		if (data == null) {	//If no data on clipboard, let's call it a day. 
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
	
	/**
	 * This is only called when we are on API level < Honeycomb
	 * @param context The context, required to get the Cliboard System Service.  
	 * @return Returns the latest text copied to the clipboard by the user
	 */
	@SuppressWarnings("deprecation")
	private static String getTextFromClipboard(final Context context) {
		android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		String text = (String) manager.getText();	//Yep, that much simpler. 
		if (text == null) {
			text = EMPTY_STRING;
		}
		return text;
	}
	
	/**
	 * 
	 * @param context The context, required to get the Cliboard System Service.
	 * @param oldAPI Set to true when running API level lower than Honeycomb
	 * @return The last text copied to the clipboard by the user. 
	 */
	public static String getLastClip (final Context context, boolean oldAPI) {		
		
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
