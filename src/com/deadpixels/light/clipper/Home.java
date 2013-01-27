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
package com.deadpixels.light.clipper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.deadpixels.light.clipper.adapters.ClipsAdapter;
import com.deadpixels.light.clipper.utils.ClipHelper;

public class Home extends Activity {

	public static final String TAG = "Light Clipper";

	private ListView clipList;
	private ClipsAdapter mAdapter;
	private ArrayList<String> recentClips;
	private boolean isOldAPI = true;
	private SharedPreferences mPreferences;
	private boolean linksClickable; 
	public static boolean darkThemeEnabled;
		
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.cliplist_context_menu, menu);
	}	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_add_to_board:
	        	String value = mAdapter.getItem(info.position);
				ClipHelper.addItemToClipboard(Home.this, "label", value, isOldAPI);
				Toast.makeText(Home.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.menu_delete_from_board:
	        	mAdapter.removeItem(info.position);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updatePerfDependentValues();

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			isOldAPI = false;
		}
		
		setContentView(R.layout.activity_home);
		
		Intent intent = getIntent();

		if (intent.getExtras() != null) {
			String action = getIntent().getAction();
			String data = "";
			if (intent.hasExtra(Intent.EXTRA_TEXT)) {
				data = getIntent().getCharSequenceExtra(Intent.EXTRA_TEXT).toString();
				ClipHelper.addItemToClipboard(this, action, data, isOldAPI);
				Toast.makeText(this, "Data copied to clipboard", Toast.LENGTH_SHORT).show();
				this.finish();
			}
		}

		recentClips = new ArrayList<String>();		
		recentClips = ClipHelper.getSavedClips(this);		

		initViews();
	}
	
	private void updatePerfDependentValues() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		linksClickable = mPreferences.getBoolean(Settings.KEY_PREF_MAKE_LINKS_CLICKABLE, false);
		darkThemeEnabled = mPreferences.getBoolean(Settings.KEY_PREF_CUR_THEME, false);

		if (darkThemeEnabled) {
			Log.v(TAG, "Attempting to set dark theme");
			setTheme(R.style.DarkTheme);
		}
		else if (!darkThemeEnabled) {
			setTheme(R.style.LightTheme);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent settingsIntent = new Intent(Home.this, Settings.class);
			startActivity(settingsIntent);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getLastItemFromClipboard(isOldAPI, false);
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "Stopping");
		ClipHelper.saveClips(this, recentClips);		
		super.onStop();
	}

	private void initViews () {
		clipList = (ListView) findViewById(R.id.home_clip_list); 
		registerForContextMenu(clipList);		
		mAdapter = new ClipsAdapter(this, recentClips, linksClickable);				
		clipList.setAdapter(mAdapter);		
	}

	public void getLastItemFromClipboard (boolean oldAPI, boolean toast) {
		String lastClip = ClipHelper.getLastClip(this, oldAPI);
		if (lastClip != "" && !recentClips.contains(lastClip)) {
			recentClips.add(lastClip);
			mAdapter.addItem(lastClip);
			mAdapter.notifyDataSetChanged();
		}

		if (toast) {
			Toast.makeText(this,"Text: " + lastClip, Toast.LENGTH_LONG).show();
		}
	}
	
}
