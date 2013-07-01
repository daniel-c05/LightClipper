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
package com.thoughts.apps.clipper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.thoughts.apps.clipper.adapters.ClipsArrayAdapter;
import com.thoughts.apps.clipper.database.DbManager;
import com.thoughts.apps.clipper.database.DbOpenHelper;
import com.thoughts.apps.clipper.ui.ClipListItem;
import com.thoughts.apps.clipper.utils.ClipHelper;

/**
 * @author Daniel Alvarado
 *
 */
public class Home extends Activity implements OnItemClickListener {

	public static final String TAG = "Light Clipper";

	private ListView clipList;
	private ClipsArrayAdapter mAdapter;
	private SharedPreferences mPreferences;
	private boolean darkThemeEnabled;


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
			String value = mAdapter.getString(info.position);
			ClipHelper.addItemToClipboard(Home.this, "label", value);
			Toast.makeText(Home.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_delete_from_board:
			if (mAdapter.getCount() == 1 || mAdapter.getCount() - 1 == info.position) {
				Log.v(TAG, "Clearing clipboard");
				ClipHelper.clearClipboard(Home.this);
			}

            DbManager.deleteClip(Home.this, mAdapter.getString(info.position));
            mAdapter.swapCursor(DbManager.getSavedClips(this));
            mAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updatePerfDependentValues();
		initViews();
	}

	private void updatePerfDependentValues() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		darkThemeEnabled = mPreferences.getBoolean(Settings.KEY_PREF_CUR_THEME, false);

		if (darkThemeEnabled) {
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
		getLastItemFromClipboard(false);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void initViews () {
		setContentView(R.layout.activity_home);
		clipList = (ListView) findViewById(R.id.home_clip_list);
		registerForContextMenu(clipList);
		clipList.setOnItemClickListener(this);
		mAdapter = new ClipsArrayAdapter(this, R.layout.clip_list_item,
                DbManager.getSavedClips(this), new String[] {DbOpenHelper.CLIP_TEXT},
                new int[] {R.id.clip_list_text}, 0);
		clipList.setAdapter(mAdapter);
		clipList.setEmptyView(findViewById(R.id.empty));
	}

	public void getLastItemFromClipboard (boolean toast) {
		String lastClip = ClipHelper.getLastClip(this);
		if (lastClip.length() > 0) {
			DbManager.saveClip(this, lastClip);
			mAdapter.swapCursor(DbManager.getSavedClips(this));
			mAdapter.notifyDataSetChanged();
		}

		if (toast) {
			Toast.makeText(this,"Text: " + lastClip, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		if (view != null) {
            ((ClipListItem) view).onClipItemClick();
        }
	}

}
