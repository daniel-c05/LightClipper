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
package com.thoughts.apps.clipper.utils;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Helper methods to get, set, save locally, and restore clipboard items. 
 * @author Daniel Alvarado
 *
 */
public class ClipHelper {

	public static final String EMPTY_STRING = "";

	/**
	 * @param context The context, required to get the Cliboard System Service. 
	 * @param label The label to show to the user via {@code ClipDescription}
	 * @param value The value to store on the clipboard. 
	 */
	public static void addItemToClipboard (final Context context, final String label, final String value) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData.Item item = new Item(value);
		ClipDescription description = new ClipDescription(label, new String [] {ClipDescription.MIMETYPE_TEXT_PLAIN});
		ClipData data = new ClipData(description, item);
		manager.setPrimaryClip(data);				
	}

	/**
	 * This is only called when we are on API level > Honeycomb
	 * @param context The context, required to get the Cliboard System Service. 
	 * @return Returns the latest item added to the clipboard by the user, as long as the {@code MimeType} is {@code MIMETYPE_TEXT_PLAIN}
	 */
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
	 * 
	 * @param context The context, required to get the Cliboard System Service.
	 * @return The last text copied to the clipboard by the user. 
	 */
	public static String getLastClip (final Context context) {
		return getItemFromCliboard(context);
	}

    @SuppressWarnings("deprecation")
    public static void clearClipboard(Context context) {
        android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setText(EMPTY_STRING);
    }
}
