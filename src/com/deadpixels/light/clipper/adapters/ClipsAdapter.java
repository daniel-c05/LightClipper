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
package com.deadpixels.light.clipper.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.deadpixels.light.clipper.R;
import com.deadpixels.light.clipper.ui.ClipListItem;

public class ClipsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<String> mClips;	

	public ClipsAdapter (Context context, ArrayList<String> clips) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (clips != null) {
			mClips = clips;
		}
		else {
			mClips = new ArrayList<String>();
		}		
	}

	@Override
	public int getCount() {
		if (mClips == null) {
			return 0;
		}
		return mClips.size();
	}

	@Override
	public String getItem(int pos) {	
		return mClips.get(pos);
	}

	public void removeItem (int pos) {
		if (mClips == null) {
			return;
		}
		mClips.remove(pos);
		notifyDataSetChanged();
	}

	public void addItem (String item) {
		if (!mClips.contains(item)) {
			mClips.add(item);
			notifyDataSetChanged();
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.clip_list_item, null);
		}
		ClipListItem item = (ClipListItem) view;
		item.bind(mClips.get(position), position);
		
		return item;
	}	

}
