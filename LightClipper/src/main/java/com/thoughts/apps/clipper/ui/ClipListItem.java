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
package com.thoughts.apps.clipper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughts.apps.clipper.Constants;
import com.thoughts.apps.clipper.R;

public class ClipListItem extends LinearLayout {

	public TextView clipContent;
	Context mContext;

	public ClipListItem(Context context) {
		super(context);
		mContext = context;
	}

	public ClipListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}        

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		clipContent = (TextView) findViewById(R.id.clip_list_text);
	}

	/**
	 * I so copied this from the Android's MMS app. Line 608
	 * Code here: https://github.com/rascarlo/platform_packages_apps_mms/blob/jb-ras-mr1/src/com/android/mms/ui/MessageListItem.java
	 */
	public void onClipItemClick() {
		final URLSpan[] spans = clipContent.getUrls();
		Log.v(Constants.TAG, "Found : " + spans.length + " links in your textview");
		if (spans.length == 0) {

		} else if (spans.length == 1) {
			spans[0].onClick(clipContent);
		} else {
			ArrayAdapter<URLSpan> adapter =
					new ArrayAdapter<URLSpan>(mContext, android.R.layout.select_dialog_item, spans) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					try {
						URLSpan span = getItem(position);
						String url = span.getURL();
						Uri uri = Uri.parse(url);
						TextView tv = (TextView) v;
						Drawable d = mContext.getPackageManager().getActivityIcon(
								new Intent(Intent.ACTION_VIEW, uri));
						if (d != null) {
							d.setBounds(0, 0, d.getIntrinsicHeight(), d.getIntrinsicHeight());
							tv.setCompoundDrawablePadding(10);
							tv.setCompoundDrawables(d, null, null, null);
						}
						tv.setText(url);
					} catch (android.content.pm.PackageManager.NameNotFoundException ex) { 
					}
					return v;
				}
			};

			AlertDialog.Builder b = new AlertDialog.Builder(mContext);

			DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(DialogInterface dialog, int which) {
					if (which >= 0) {
						spans[which].onClick(clipContent);
					}
					dialog.dismiss();
				}
			};

			b.setTitle(R.string.title_span_selector);
			b.setCancelable(true);
			b.setAdapter(adapter, click);

			b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			b.show();
		}		
	}

	public void bind(String text) {
		setClickable(false);
		setLongClickable(false);		
		clipContent.setText(text);		
	}
}
